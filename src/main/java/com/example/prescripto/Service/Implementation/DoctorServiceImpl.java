package com.example.prescripto.Service.Implementation;

import com.example.prescripto.DTO.*;
import com.example.prescripto.Model.AppointmentEntity;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Model.UserEntity;
import com.example.prescripto.Repo.AppointmentRepo;
import com.example.prescripto.Repo.DoctorRepo;
import com.example.prescripto.Repo.UserRepo;
import com.example.prescripto.Service.Appointment;
import com.example.prescripto.Service.DoctorService;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Appointment appointmentService;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public APIResponse getAllDoctors() {
        List<DoctorEntity> allDoctors=doctorRepo.findAll();
        return new APIResponse(true,allDoctors);
    }

    @Override
    public APIResponse doctorLogin(AdminDTO doctorDTO){
         DoctorEntity doctor = doctorRepo.findByEmail(doctorDTO.getEmail());
            if (doctor == null) {
                return new APIResponse(false, "Doctor not found");
            }

        if (passwordEncoder.matches(doctorDTO.getPassword(),doctor.getPassword())) {
            String token = jwtUtil.generateToken(doctor.getEmail());
            return new APIResponse(true, "Doctor Logged In Successfully!!", token);
        } else {
            return new APIResponse(false, "Invalid Credentials!!");
        }

    }

    @Override
    public APIResponse getAllAppointments(String authHeader) {
        String email = jwtUtil.extractUsername(authHeader.substring(7));
        DoctorEntity doctor = doctorRepo.findByEmail(email);
        return appointmentService.getAppointmentsForDoctor(doctor.getId());
    }

    @Override
   // @Cacheable(value = "doctorProfile", key = "#email")
    public APIResponse getDoctorProfile(String email) {
        System.out.println("Fetching doctor profile for email: ");
        DoctorEntity doctor = doctorRepo.findByEmail(email);
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setName(doctor.getName());
        doctorDTO.setSpeciality(doctor.getSpeciality());
        doctorDTO.setImageUrl(doctor.getImage());
        doctorDTO.setAvailable(doctor.getAvailable());
        doctorDTO.setAddress(doctor.getAddress());
        doctorDTO.setAbout(doctor.getAbout());
        doctorDTO.setFees(doctor.getFees());
        return new APIResponse(true, doctorDTO);
    }

    @Override
    @Transactional
//    @Caching(evict = {
//            @CacheEvict(value = "doctorsList", allEntries = true, beforeInvocation = true),
//            @CacheEvict(value = "doctorProfile", key = "#email")
//    })
    public APIResponse updateDoctorProfile(String email, DoctorDTO doctorDTO) {
        DoctorEntity doctor = doctorRepo.findByEmail(email);
        if (doctor == null) {
            return new APIResponse(false, "Doctor not found");
        }

            doctor.setAbout(doctorDTO.getAbout());
            doctor.setAddress(doctorDTO.getAddress());
            doctor.setFees(doctorDTO.getFees());
            doctor.setAvailable(doctorDTO.getAvailable());
            doctorRepo.save(doctor);
            return new APIResponse(true, "Profile Updated Successfully!!");
    }

    @Override
    public APIResponse getDoctorDashboardData(String email) {
            DoctorEntity doctor = doctorRepo.findByEmail(email);
            if(doctor == null){
                return new APIResponse(false, "Doctor not found");
            }

            List<AppointmentEntity> appointments = appointmentRepo.findByDocId(doctor.getId());

            Long earnings = appointments.stream()
                    .filter(a -> !a.getCancelled())
                    .filter(b -> b.getPayment() || b.getCompleted())
                    .count() * doctor.getFees();

        System.out.println("Total Earnings: " + earnings);
        HashSet<Long> uniquePatients = new HashSet<>();
        DoctorDashBoardDTO doctorDashBoardDTO = new DoctorDashBoardDTO();
        List<DoctorAppointmentsDTO> latest = new java.util.ArrayList<>();

        for(AppointmentEntity a : appointments){
            uniquePatients.add(a.getUserId());
            Optional<UserEntity> userOpt = userRepo.findById(a.getUserId());
            DoctorAppointmentsDTO doctorAppointmentsDTO = new DoctorAppointmentsDTO();
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                doctorAppointmentsDTO.setName(user.getName());
                doctorAppointmentsDTO.setImage(user.getImage());
            } else {
                doctorAppointmentsDTO.setName("Unknown");
                doctorAppointmentsDTO.setImage(null);
            }
            doctorAppointmentsDTO.setSlotDate(a.getSlotDate());
            doctorAppointmentsDTO.setCancelled(a.getCancelled());
            doctorAppointmentsDTO.setCompleted(a.getCompleted());
            doctorAppointmentsDTO.setPayment(a.getPayment());
            latest.add(doctorAppointmentsDTO);
        }


        doctorDashBoardDTO.setAppointments((long) appointments.size());
        doctorDashBoardDTO.setEarnings(earnings);
        doctorDashBoardDTO.setPatients((long) uniquePatients.size());
        doctorDashBoardDTO.setLatestAppointments(latest);


        return new APIResponse(true, doctorDashBoardDTO);
    }

    @Override
    public APIResponse cancelAppointment(Long appointmentId) {

        return appointmentService.calcelAppointment(String.valueOf(appointmentId));
    }

    @Override
    public APIResponse completeAppointment(Long appointmentId) {
        return appointmentService.completeAppointment(String.valueOf(appointmentId));

    }
}
