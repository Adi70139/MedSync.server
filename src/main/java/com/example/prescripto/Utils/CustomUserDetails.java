package com.example.prescripto.Utils;


import com.example.prescripto.Model.UserEntity;
import com.example.prescripto.Repo.DoctorRepo;
import com.example.prescripto.Repo.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetails implements UserDetailsService {

    private final UserRepo userRepo;

    private final DoctorRepo doctorRepo;

    public CustomUserDetails(UserRepo userRepo, DoctorRepo doctorRepo) {
        this.userRepo = userRepo;
        this.doctorRepo = doctorRepo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First try to find a regular user
        Optional<UserEntity> user = userRepo.findByEmail(email);
        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),
                    user.get().getPassword(),
                    Collections.emptyList()
            );
        }

        // If not a regular user, try to find a doctor
        com.example.prescripto.Model.DoctorEntity doctor = doctorRepo.findByEmail(email);
        if (doctor != null) {
            return new org.springframework.security.core.userdetails.User(
                    doctor.getEmail(),
                    doctor.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"))
            );
        }

        // Not found in either repository
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
