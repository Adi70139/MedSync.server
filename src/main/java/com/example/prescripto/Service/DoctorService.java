package com.example.prescripto.Service;

import com.example.prescripto.DTO.AdminDTO;
import com.example.prescripto.DTO.DoctorDTO;
import com.example.prescripto.Utils.APIResponse;

public interface DoctorService {

      APIResponse getAllDoctors();

    APIResponse doctorLogin(AdminDTO doctorDTO);

    APIResponse getAllAppointments(String authHeader);

    APIResponse getDoctorProfile(String email);

    APIResponse updateDoctorProfile(String email, DoctorDTO doctorDTO);

    APIResponse getDoctorDashboardData(String email);

    APIResponse cancelAppointment(Long appointmentId);

    APIResponse completeAppointment(Long id);
}
