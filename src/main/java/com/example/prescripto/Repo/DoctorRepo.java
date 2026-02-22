package com.example.prescripto.Repo;

import com.example.prescripto.Model.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepo extends JpaRepository<DoctorEntity,Long> {
    DoctorEntity findByEmail(String email);
}
