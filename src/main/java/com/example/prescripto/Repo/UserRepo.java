package com.example.prescripto.Repo;

import com.example.prescripto.Model.UserEntity;
import com.example.prescripto.Utils.APIResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity,Long> {

       Optional<UserEntity> findByEmail(String email);
}
