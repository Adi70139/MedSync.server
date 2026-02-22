package com.example.prescripto.Controller;

import com.example.prescripto.DTO.AdminDTO;
import com.example.prescripto.DTO.DoctorDTO;
import com.example.prescripto.DTO.IdDTO;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Service.AdminService;
import com.example.prescripto.Utils.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

@RestController
@Component
@CrossOrigin(origins = "*")
public class AdminController {

      @Autowired
      private AdminService adminService;


       @PostMapping("/api/admin/login")
       public APIResponse login(@RequestBody AdminDTO adminDTO){

            return adminService.login(adminDTO);
       }

       @PostMapping(value = "/api/admin/add-doctor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
       public APIResponse addDoctor(@ModelAttribute DoctorDTO doctorDTO){
           System.out.println("Received DoctorDTO: " + doctorDTO);
            return adminService.addDoctor(doctorDTO);
       }

       @GetMapping("/api/admin/all-doctors")
       public APIResponse getAllDoctors(){
           List<DoctorDTO> doctors=adminService.getAllDoctors();
              if(doctors.size()<0) {
                  return new APIResponse(false, "No Doctors Found");
              }

              HashMap<String, List<DoctorDTO>> doctorsMap= new HashMap<>();
              doctorsMap.put("doctors", doctors);
              return new APIResponse(true, doctorsMap);
       }

    @PostMapping("/api/admin/change-availability")
    public APIResponse changeAvailability(@RequestBody IdDTO idDTO){
           adminService.changeAvailability(idDTO.getId());
         return new APIResponse(true,"Doctor Availability Changed Successfully!!");
    }


    @GetMapping("/api/admin/appointments")
    public APIResponse getAllAppointments(@RequestHeader("Authorization") String authHeader){
            return adminService.getAllAppointments();
       }

    @PostMapping("/api/admin/cancel-appointment")
    public APIResponse cancelAppointment(@RequestHeader("Authorization") String authHeader,@RequestBody IdDTO idDTO){
              return adminService.calcelAppointment(idDTO.getId());
       }


    @GetMapping("/api/admin/dashboard")
    public APIResponse getDashboardData(@RequestHeader("Authorization") String authHeader){
                return adminService.getDashboardData();
       }
}
