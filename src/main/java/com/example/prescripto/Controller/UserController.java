package com.example.prescripto.Controller;


import com.example.prescripto.DTO.BookAppointmentRequest;
import com.example.prescripto.DTO.UserDTO;
import com.example.prescripto.Service.UserService;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController()
@Component
@CrossOrigin(origins = "*")
public class UserController {

       @Autowired
       private UserService userService;

       @Autowired
       private JwtUtil jwtUtil;

       @PostMapping("/api/user/register")
       public APIResponse register(@RequestBody UserDTO userDTO){
           return userService.resgisterUser(userDTO);
       }

       @PostMapping("/api/user/login")
       public APIResponse login(@RequestBody UserDTO userDTO){
           return userService.loginUser(userDTO);
       }

       @GetMapping("/api/user/get-profile")
       public APIResponse getAllUsers(@RequestHeader("Authorization") String authHeader){
           String token = authHeader.substring(7);
           String email= jwtUtil.extractUsername(token);
           return userService.findByEmail(email);
       }

       @PostMapping(value = "/api/user/update-profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
       public APIResponse updateUserProfile(@RequestHeader("Authorization") String authHeader,@ModelAttribute UserDTO userDTO){
           String token = authHeader.substring(7);
           String email= jwtUtil.extractUsername(token);
           System.out.println(email);
           return userService.updateUserProfile(email,userDTO);
       }

       @GetMapping("/api/user/appointments")
       public APIResponse getUserAppointments(@RequestHeader("Authorization") String authHeader){
           String token = authHeader.substring(7);
           String email= jwtUtil.extractUsername(token);
           return userService.getUserAppointments(email);
       }


    @PostMapping("/api/user/book-appointment")
    public APIResponse bookAppointment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BookAppointmentRequest request
    ) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        return userService.bookAppointment(
                email,
                request.getDocId(),
                request.getSlotDate(),
                request.getSlotTime(),
                request.getAmount()
        );
    }






}
