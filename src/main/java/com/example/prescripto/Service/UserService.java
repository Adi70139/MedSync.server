package com.example.prescripto.Service;

import com.example.prescripto.DTO.UserDTO;
import com.example.prescripto.Utils.APIResponse;

public interface UserService {

     APIResponse resgisterUser(UserDTO userDTO);

     APIResponse loginUser(UserDTO userDTO);

     APIResponse findByEmail(String email);

     APIResponse updateUserProfile(String userEmail,UserDTO userDTO);

    APIResponse getUserAppointments(String email);

    APIResponse bookAppointment(String email, Long doctorId, String slotDate, String slotTime, Long amount);


}
