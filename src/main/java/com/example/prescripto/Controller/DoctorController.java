package com.example.prescripto.Controller;

import com.example.prescripto.DTO.AdminDTO;
import com.example.prescripto.DTO.DoctorDTO;
import com.example.prescripto.DTO.IdDTO;
import com.example.prescripto.Service.DoctorService;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
public class DoctorController {



      @Autowired
      private DoctorService doctorService;

      @Autowired
      private JwtUtil jwtUtil;

      @GetMapping("/api/doctor/list")
      public APIResponse getAllDoctors(){
           return doctorService.getAllDoctors();
      }

      @PostMapping("/api/doctor/login")
      public APIResponse doctorLogin(@RequestBody AdminDTO doctorDTO){
            return doctorService.doctorLogin(doctorDTO);
      }


      @GetMapping("/api/doctor/appointments")
      public APIResponse getAllAppointments(@RequestHeader("Authorization") String authHeader){

            return doctorService.getAllAppointments(authHeader);
      }

      @GetMapping("/api/doctor/profile")
      public APIResponse getDoctorProfile(@RequestHeader("Authorization") String authHeader){
            String email = jwtUtil.extractUsername(authHeader.substring(7));
            return doctorService.getDoctorProfile(email);
      }


      @PostMapping("/api/doctor/update-profile")
      public APIResponse updateDoctorProfile(@RequestHeader("Authorization") String authHeader, @RequestBody DoctorDTO doctorDTO){
            String email = jwtUtil.extractUsername(authHeader.substring(7));

            return doctorService.updateDoctorProfile(email, doctorDTO);
      }

      @GetMapping("/api/doctor/dashboard")
      public APIResponse getDoctorDashboardData(@RequestHeader("Authorization") String authHeader){
            String email = jwtUtil.extractUsername(authHeader.substring(7));
            return doctorService.getDoctorDashboardData(email);
      }


      @PostMapping("/api/doctor/cancel-appointment")
      public APIResponse cancelAppointment(@RequestHeader("Authorization") String authHeader, @RequestBody IdDTO idDTO){
            String email = jwtUtil.extractUsername(authHeader.substring(7));
            return doctorService.cancelAppointment(idDTO.getId());
      }


      @PostMapping("/api/doctor/complete-appointment")
      public APIResponse completeAppointment(@RequestHeader("Authorization") String authHeader, @RequestBody IdDTO idDTO){
            String email = jwtUtil.extractUsername(authHeader.substring(7));
            return doctorService.completeAppointment(idDTO.getId());
      }




}
