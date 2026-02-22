package com.example.prescripto.Service;

import com.example.prescripto.DTO.AdminDTO;
import com.example.prescripto.DTO.DoctorDTO;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Utils.APIResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {

       APIResponse login(AdminDTO adminDTO);

       APIResponse addDoctor(DoctorDTO doctorDTO);

       List<DoctorDTO> getAllDoctors();

       DoctorDTO changeAvailability(Long docId);

       APIResponse getAllAppointments();

       APIResponse calcelAppointment(Long id);

       APIResponse getDashboardData();

}
