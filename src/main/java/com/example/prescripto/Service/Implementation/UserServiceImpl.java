package com.example.prescripto.Service.Implementation;

import com.example.prescripto.DTO.UserDTO;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Model.UserEntity;
import com.example.prescripto.Repo.DoctorRepo;
import com.example.prescripto.Repo.UserRepo;
import com.example.prescripto.Service.Appointment;
import com.example.prescripto.Service.UserService;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.Address;
import com.example.prescripto.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.sound.midi.Soundbank;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Appointment appointmentService;

    @Override
    public APIResponse resgisterUser(UserDTO userDTO) {

        if (userRepo.findByEmail(userDTO.getEmail()).isPresent()) {
            return new APIResponse(false, "User Already Exists!!");
        }

        UserEntity newUser = new UserEntity();
        newUser.setEmail(userDTO.getEmail());
        newUser.setName(userDTO.getName());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setPhoneNumber(0000000000L);
        newUser.setDob("Not Selected");
        newUser.setGender("Not Selected");
        newUser.setImage("user.png");
        Address address=new Address();
        address.setAddress1(userDTO.getAddress1());
        address.setAddress2(userDTO.getAddress2());
        newUser.setAddress(address);
        userRepo.save(newUser);
        String token = jwtUtil.generateToken(newUser.getEmail());
        return new APIResponse(true, "User Registered Successfully!!", token);

    }

    @Override
    @Cacheable(value = "users")
    public APIResponse findByEmail(String email) {
        System.out.println("Fetching from DB");
        Optional<UserEntity> u1 = userRepo.findByEmail(email);
        if (u1.isPresent()) {
            UserEntity user= u1.get();
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

            UserDTO currUser=new UserDTO();
            currUser.setImage(baseUrl+"/uploads/"+user.getImage());
            currUser.setName(user.getName());
            currUser.setEmail(user.getEmail());
            Address address= user.getAddress();
            currUser.setAddress1(address.getAddress1());
            currUser.setAddress2(address.getAddress2());
            currUser.setDob(user.getDob());
            currUser.setGender(user.getGender());
            currUser.setPhone(user.getPhoneNumber());
            return new APIResponse(true,currUser);
        }
        return new APIResponse(false, "User Not Found!!");
    }

    @Override
    public APIResponse loginUser(UserDTO userDTO) {
        Optional<UserEntity> user = userRepo.findByEmail(userDTO.getEmail());
        if (user.isPresent()) {
            if (passwordEncoder.matches(userDTO.getPassword(), user.get().getPassword())) {
                String token = jwtUtil.generateToken(user.get().getEmail());
                return new APIResponse(true, "User Logged In Successfully!!", token);
            } else {
                return new APIResponse(false, "Invalid Credentials!!");
            }
        }

        return new APIResponse(false, "User Not Found!!");
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public APIResponse updateUserProfile(String userEmail, UserDTO userDTO) {
        Optional<UserEntity> user=userRepo.findByEmail(userEmail);
        UserEntity currUser = user.get();
        currUser.setDob(userDTO.getDob());
        currUser.setGender(userDTO.getGender());
        currUser.setPhoneNumber(userDTO.getPhone());
        Address address=new Address();
        address.setAddress1(userDTO.getAddress1());
        address.setAddress2(userDTO.getAddress2());
        currUser.setAddress(address);
        userRepo.save(currUser);
        return new APIResponse(true,"Profile updated successfully");
    }

    @Override
    public APIResponse getUserAppointments(String email) {
        UserEntity user=userRepo.findByEmail(email).get();
        return appointmentService.getUserAppointments(user.getId());
    }

    @Override
    public APIResponse bookAppointment(String email, Long doctorId, String slotDate, String slotTime, Long amount){
        UserEntity user=userRepo.findByEmail(email).get();

        return appointmentService.bookAppointment(user.getId(),email,doctorId,slotDate,slotTime,amount);
    }
}
