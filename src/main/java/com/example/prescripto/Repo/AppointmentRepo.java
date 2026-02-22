package com.example.prescripto.Repo;

import com.example.prescripto.Model.AppointmentEntity;
import com.example.prescripto.Utils.APIResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepo extends JpaRepository<AppointmentEntity, Long> {

    boolean existsByDocIdAndSlotDateAndSlotTime(
            Long docId, String slotDate, String slotTime
    );

    AppointmentEntity findByDocIdAndSlotDateAndSlotTime(
            Long docId, String slotDate, String slotTime
    );
    List<AppointmentEntity> findByUserId(Long userId);

    // return latest 5 appointments by id (proxy for newest)
    List<AppointmentEntity> findTop5ByOrderByIdDesc();

    List<AppointmentEntity> findByDocId(Long id);
}
