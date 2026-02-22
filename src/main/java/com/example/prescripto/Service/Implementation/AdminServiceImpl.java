package com.example.prescripto.Service.Implementation;


import com.example.prescripto.DTO.AdminDTO;
import com.example.prescripto.DTO.AllAppointmentsResDTO;
import com.example.prescripto.DTO.DoctorDTO;
import com.example.prescripto.DTO.UserDTO;
import com.example.prescripto.Model.AppointmentEntity;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Model.UserEntity;
import com.example.prescripto.Repo.AppointmentRepo;
import com.example.prescripto.Repo.DoctorRepo;
import com.example.prescripto.Repo.UserRepo;
import com.example.prescripto.Service.AdminService;
import com.example.prescripto.Service.Appointment;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.Address;
import com.example.prescripto.Utils.JwtUtil;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Value("${adminEmail}")
    private String ADMIN_USERNAME;

    @Value("${adminPassword}")
    private String ADMIN_PASSWORD;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Appointment appointmentService;

    @Autowired
    private CacheManager cacheManager;



    @Override
    public APIResponse login(AdminDTO adminDTO) {

        String email = adminDTO.getEmail();

        if (!email.equals(ADMIN_USERNAME) || !adminDTO.getPassword().equals(ADMIN_PASSWORD)) {
            return new APIResponse(false, "Invalid Credentials");
        }

        String token = jwtUtil.generateToken(email);
        return new APIResponse(true, "Admin Logged In Successfully!!", token);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "doctorsList", allEntries = true),
            @CacheEvict(value = "doctorProfile", key = "#doctorDTO.email")
    })
    public APIResponse addDoctor(DoctorDTO doctorDTO) {

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setName(doctorDTO.getName());
        doctorEntity.setEmail(doctorDTO.getEmail());
        doctorEntity.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));

        String imageUrl = saveImage(doctorDTO.getImage());
        doctorEntity.setImage(imageUrl);
        doctorEntity.setSpeciality(doctorDTO.getSpeciality());
        doctorEntity.setDegree(doctorDTO.getDegree());
        doctorEntity.setExperience(doctorDTO.getExperience());
        doctorEntity.setAbout(doctorDTO.getAbout());
        doctorEntity.setFees(doctorDTO.getFees());

        Address address = new Address();
        address.setAddress1(doctorDTO.getAddress1());
        address.setAddress2(doctorDTO.getAddress2());

        doctorEntity.setAddress(address);
        doctorRepo.save(doctorEntity);

        // Ensure explicit eviction of any cached profile for this email (in case it existed)
        if (cacheManager != null && doctorDTO != null && doctorDTO.getEmail() != null) {
            try {
                if (cacheManager.getCache("doctorProfile") != null) {
                    cacheManager.getCache("doctorProfile").evict(doctorDTO.getEmail());
                }
            } catch (Exception ignored) {
            }
        }

        return new APIResponse(true, "Doctor Added Successfully!!");
    }


    public String saveImage(MultipartFile imageFile) {
        String baseUrl=ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        Path uploadPath = Paths.get("uploads");

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            } else {
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
        return baseUrl+"/uploads/"+fileName;
    }

    @Override
    @Cacheable(value="doctorsList")
    public List<DoctorDTO> getAllDoctors() {
        System.out.println("Fetching doctors from DB");
        List<DoctorEntity> doctorEntities = doctorRepo.findAll();
        return doctorEntities.stream().map(doctor->{
            DoctorDTO dto= new DoctorDTO();
            dto.setName(doctor.getName());
            dto.setSpeciality(doctor.getSpeciality());
            dto.setAvailable(doctor.getAvailable());
            dto.setId(doctor.getId());
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            dto.setImageUrl(doctor.getImage());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "doctors", key = "#id")
    @CacheEvict(value = "doctorsList", allEntries = true, beforeInvocation = true)
    public DoctorDTO changeAvailability(Long id) {
        DoctorEntity doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor Not Found!!"));

        doctor.setAvailable(!doctor.getAvailable());
        doctorRepo.save(doctor);

        // evict the doctor's profile cache so subsequent profile reads reflect availability change
        try {
            if (cacheManager != null && doctor.getEmail() != null && cacheManager.getCache("doctorProfile") != null) {
                cacheManager.getCache("doctorProfile").evict(doctor.getEmail());
            }
        } catch (Exception ignored) {
        }

        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setSpeciality(doctor.getSpeciality());
        dto.setAvailable(doctor.getAvailable());
        dto.setImageUrl(doctor.getImage());
        return dto;

    }

    @Override
    public APIResponse getAllAppointments() {
        List<AppointmentEntity> appointments = appointmentRepo.findAll();

        List<AllAppointmentsResDTO> res = new ArrayList<>();

        for(AppointmentEntity appointment : appointments) {
            UserEntity user = userRepo.findById(appointment.getUserId()).orElseThrow(() -> new RuntimeException("User Not Found!!"));
            DoctorEntity doctor = doctorRepo.findById(appointment.getDocId()).orElseThrow(() -> new RuntimeException("Doctor Not Found!!"));
            UserDTO userDTO = new UserDTO();
            userDTO.setName(user.getName());
            userDTO.setDob(user.getDob());
            userDTO.setImage(user.getImage());

            DoctorDTO doctorDTO = new DoctorDTO();
            doctorDTO.setName(doctor.getName());
            doctorDTO.setImageUrl(doctor.getImage());
            AllAppointmentsResDTO resDTO = new AllAppointmentsResDTO();
            resDTO.setCancelled(appointment.getCancelled());
            resDTO.setCompleted(appointment.getCompleted());

            resDTO.setDoctorData(doctorDTO);
            resDTO.setUserData(userDTO);
            resDTO.setSlotDate(appointment.getSlotDate());
            resDTO.setSlotTime(appointment.getSlotTime());
            resDTO.setId(appointment.getId());
            resDTO.setAmount(appointment.getAmount());
            // add top-level name and image fields (use doctor name/image when available otherwise user)
            if (doctor != null) {
                resDTO.setName(doctor.getName());
                resDTO.setImage(doctor.getImage());
            } else if (user != null) {
                resDTO.setName(user.getName());
                resDTO.setImage(user.getImage());
            }
            res.add(resDTO);
        }
        return new APIResponse(true, res);
    }

    @Override
    public APIResponse calcelAppointment(Long id) {
        // delegate to centralized appointment service so rules (payment check, metadata update) are enforced uniformly
        return appointmentService.calcelAppointment(String.valueOf(id));
    }

    @Override
    public APIResponse getDashboardData() {
        long doctorsCount = doctorRepo.count();
        long usersCount = userRepo.count();
        long appointmentsCount = appointmentRepo.count();

        List<AppointmentEntity> latestAppointments = appointmentRepo.findTop5ByOrderByIdDesc();

        List<AllAppointmentsResDTO> res = new ArrayList<>();

        for (AppointmentEntity appointment : latestAppointments) {
            UserEntity user = userRepo.findById(appointment.getUserId()).orElse(null);
            DoctorEntity doctor = doctorRepo.findById(appointment.getDocId()).orElse(null);

            UserDTO userDTO = new UserDTO();
            if (user != null) {
                userDTO.setName(user.getName());
                userDTO.setDob(user.getDob());
                userDTO.setImage(user.getImage());
            }

            DoctorDTO doctorDTO = new DoctorDTO();
            if (doctor != null) {
                doctorDTO.setName(doctor.getName());
                doctorDTO.setImageUrl(doctor.getImage());
            }

            AllAppointmentsResDTO resDTO = new AllAppointmentsResDTO();
            resDTO.setId(appointment.getId());
            resDTO.setCancelled(appointment.getCancelled());
            resDTO.setCompleted(appointment.getCompleted());
            resDTO.setAmount(appointment.getAmount());
            resDTO.setSlotDate(appointment.getSlotDate());
            resDTO.setSlotTime(appointment.getSlotTime());
            resDTO.setDoctorData(doctorDTO);
            resDTO.setUserData(userDTO);
            // populate name and image for dashboard latestAppointments
            if (doctor != null) {
                resDTO.setName(doctor.getName());
                resDTO.setImage(doctor.getImage());
            } else if (user != null) {
                resDTO.setName(user.getName());
                resDTO.setImage(user.getImage());
            }

            res.add(resDTO);
        }

        // build response map
        java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
        dashboard.put("doctors", doctorsCount);
        dashboard.put("users", usersCount);
        dashboard.put("appointments", appointmentsCount);
        dashboard.put("latestAppointments", res);

        return new APIResponse(true, dashboard);
    }


}
