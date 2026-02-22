package com.example.prescripto.Service.Implementation;

import com.example.prescripto.Adapters.AppointmentEmbeddingAdapter;
import com.example.prescripto.DTO.AppointmentResponseDTO;
import com.example.prescripto.DTO.DoctorResponseDTO;
import com.example.prescripto.DTO.GetAppointmentResponse;
import com.example.prescripto.DTO.UserDTO;
import com.example.prescripto.Model.AppointmentEntity;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Model.UserEntity;
import com.example.prescripto.Repo.AppointmentRepo;
import com.example.prescripto.Repo.DoctorRepo;
import com.example.prescripto.Repo.UserRepo;
import com.example.prescripto.Service.Appointment;
import com.example.prescripto.Service.PaymentService;
import com.example.prescripto.Service.Implementation.VectorEmbeddingServiceImpl;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.StripeResponse;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AppointmentServiceImpl implements Appointment {

       @Autowired
       private AppointmentRepo appointmentRepo;

       @Autowired
       private DoctorRepo doctorRepo;

       @Autowired
       private UserRepo userRepo;

       @Autowired
       private PaymentService paymentService;

       @Autowired
       private VectorEmbeddingServiceImpl vectorEmbeddingService;

        @Override
        public APIResponse getAllAvailableSlots() {
              return new APIResponse(true,appointmentRepo.findAll());
        }

       @Override
       @Transactional
       public APIResponse bookAppointment(Long userId, String email, Long docId, String slotDate, String slotTime,Long amount) {


            if(appointmentRepo.existsByDocIdAndSlotDateAndSlotTime(docId,slotDate,slotTime)){
                return new APIResponse(false,"Slot already booked. Please choose another slot.");
            }

            DoctorEntity doctor = doctorRepo.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            if(!doctor.getAvailable())return new APIResponse(false,"Doctor is not available for appointment");

            AppointmentEntity appointment = new AppointmentEntity();
              appointment.setUserId(userId);
              appointment.setDocId(docId);
              appointment.setSlotDate(slotDate);
              appointment.setSlotTime(slotTime);
              appointment.setAmount(amount);
              appointmentRepo.save(appointment);

            // Embed the appointment into vector DB
            try {
                System.out.println("Embedding appointment for user " + email + " with doctor " + doctor.getName());
                vectorEmbeddingService.embed(new AppointmentEmbeddingAdapter(appointment, doctor, email));
            } catch (Exception e) {
                // don't fail the booking if embedding fails; log and continue
                System.err.println("Failed to embed appointment: " + e.getMessage());
            }

           return new APIResponse(true,"Appointment booked successfully");
       }

    @Override
    public APIResponse getUserAppointments(Long userId) {

        List<GetAppointmentResponse> appointmentList = new ArrayList<>();

        for(AppointmentEntity appointment : appointmentRepo.findByUserId(userId)){

            DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();
            AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO();

             Optional<DoctorEntity> doctor= doctorRepo.findById(appointment.getDocId());
             doctorResponseDTO.setAddress(doctor.get().getAddress());
             doctorResponseDTO.setImage(doctor.get().getImage());
             doctorResponseDTO.setName(doctor.get().getName());
             doctorResponseDTO.setSpeciality(doctor.get().getSpeciality());

             appointmentResponseDTO.setId(appointment.getId());
             appointmentResponseDTO.setSlotDate(appointment.getSlotDate());
             appointmentResponseDTO.setSlotTime(appointment.getSlotTime());
             appointmentResponseDTO.setCancelled(appointment.getCancelled());
             appointmentResponseDTO.setCompleted(appointment.getCompleted());
             appointmentResponseDTO.setPayment(appointment.getPayment());

             appointmentList.add(new GetAppointmentResponse(doctorResponseDTO,appointmentResponseDTO));
        }
        return new APIResponse(true, appointmentList);
    }

    @Override
    @Transactional
    public StripeResponse makePayment(String appointmentId, String origin) throws Exception {

        AppointmentEntity appointment = appointmentRepo.findById(Long.parseLong(appointmentId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (Boolean.TRUE.equals(appointment.getCancelled())) {
            throw new RuntimeException("Appointment cancelled");
        }

        try{
           String session_url = paymentService.createStripeCheckoutSession(appointment.getId(),origin,appointment.getAmount());
            System.out.println("Stripe session URL = " + session_url);
            return new StripeResponse(true,session_url);
        }catch(Exception e){
            return new StripeResponse(false,e.getMessage());
        }

    }

    @Override
    public APIResponse verifyPayemnt(String appointmentId, String success) {

        if(success.equals("true")){
            AppointmentEntity appointment = appointmentRepo.findById(Long.parseLong(appointmentId))
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));
            appointment.setPayment(true);

            appointmentRepo.save(appointment);

            // update vector DB metadata for this appointment
            try {
                // fetch doctor to include updated metadata
                DoctorEntity doctor = doctorRepo.findById(appointment.getDocId()).orElse(null);
                vectorEmbeddingService.updateMetadata(new AppointmentEmbeddingAdapter(appointment, doctor, null));
            } catch (Exception e) {
                System.err.println("Failed to update vector metadata after payment: " + e.getMessage());
            }
            return new APIResponse(true,"Payment Successful!!");
        }

        return new APIResponse(false,"Payment Failed!!");
    }

    @Override
    public APIResponse calcelAppointment(String appointmentId) {
        AppointmentEntity appointment = appointmentRepo.findById(Long.parseLong(appointmentId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (Boolean.TRUE.equals(appointment.getPayment())) {
            return new APIResponse(false, "Cannot cancel a paid appointment!!");
        }

        if (Boolean.TRUE.equals(appointment.getCancelled())) {
            return new APIResponse(false, "Appointment already cancelled!!");
        }

        // mark cancelled instead of deleting
        appointment.setCancelled(true);
        appointmentRepo.save(appointment);

        // update vector DB metadata for this appointment (reflect cancelled=true)
        try {
            DoctorEntity doctor = doctorRepo.findById(appointment.getDocId()).orElse(null);
            vectorEmbeddingService.updateMetadata(new AppointmentEmbeddingAdapter(appointment, doctor, null));
        } catch (Exception e) {
            System.err.println("Failed to update vector metadata after cancellation: " + e.getMessage());
        }

        return new APIResponse(true, "Appointment cancelled successfully!!");
    }

    @Override
    public APIResponse completeAppointment(String appointmentId) {
            AppointmentEntity appointment = appointmentRepo.findById(Long.parseLong(appointmentId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));


        if (Boolean.TRUE.equals(appointment.getCancelled())) {
            return new APIResponse(false, "Cannot complete a cancelled appointment!!");
        }

        if (Boolean.TRUE.equals(appointment.getCompleted())) {
            return new APIResponse(false, "Appointment already completed!!");
        }

        appointment.setCompleted(true);
        appointmentRepo.save(appointment);


        // update vector DB metadata for this appointment (reflect cancelled=true)
        try {
            DoctorEntity doctor = doctorRepo.findById(appointment.getDocId()).orElse(null);
            vectorEmbeddingService.updateMetadata(new AppointmentEmbeddingAdapter(appointment, doctor, null));
        } catch (Exception e) {
            System.err.println("Failed to update vector metadata after cancellation: " + e.getMessage());
        }

        return new APIResponse(true, "Appointment completed successfully!!");
    }

    @Override
    public APIResponse getAppointmentsForDoctor(Long id) {
        List<GetAppointmentResponse> appointmentList = new ArrayList<>();

        for(AppointmentEntity appointment : appointmentRepo.findByDocId(id)){

            DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();
            AppointmentResponseDTO appointmentResponseDTO = new AppointmentResponseDTO();
            UserDTO userDTO = new UserDTO();

            Optional<DoctorEntity> doctor= doctorRepo.findById(appointment.getDocId());
            doctorResponseDTO.setAddress(doctor.get().getAddress());
            doctorResponseDTO.setImage(doctor.get().getImage());
            doctorResponseDTO.setName(doctor.get().getName());
            doctorResponseDTO.setSpeciality(doctor.get().getSpeciality());

            Optional<UserEntity> user = userRepo.findById(appointment.getUserId());
            userDTO.setDob(user.get().getDob());
            userDTO.setImage(user.get().getImage());
            userDTO.setName(user.get().getName());

            appointmentResponseDTO.setId(appointment.getId());
            appointmentResponseDTO.setSlotDate(appointment.getSlotDate());
            appointmentResponseDTO.setSlotTime(appointment.getSlotTime());
            appointmentResponseDTO.setCancelled(appointment.getCancelled());
            appointmentResponseDTO.setCompleted(appointment.getCompleted());
            appointmentResponseDTO.setPayment(appointment.getPayment());
            appointmentResponseDTO.setAmount(appointment.getAmount());

            appointmentList.add(new GetAppointmentResponse(appointmentResponseDTO,doctorResponseDTO,userDTO));
        }
        return new APIResponse(true, appointmentList);
    }




}
