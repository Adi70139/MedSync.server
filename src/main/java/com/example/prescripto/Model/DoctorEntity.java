package com.example.prescripto.Model;

import com.example.prescripto.Utils.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "doctors")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DoctorEntity {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     private String name;

     @Column(nullable = false,unique = true)
     private String email;

     @Column(nullable = false)
     private String password;

     private String image;


     private String speciality;

     private String degree;

     private String experience;

     private String about;

     private boolean available=true;

     @Column(nullable = false)
     private Long fees;

     @Embedded
     private Address address;

     private LocalDate date = LocalDate.now();


     public Long getId() {
          return id;
     }

     public void setId(Long id) {
          this.id = id;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getEmail() {
          return email;
     }


     public void setEmail(String email) {
          this.email = email;
     }

     public String getPassword() {
          return password;
     }

     public void setPassword(String password) {
          this.password = password;
     }

     public String getSpeciality() {
          return speciality;
     }

     public void setSpeciality(String speciality) {
          this.speciality = speciality;
     }

     public String getDegree() {
          return degree;
     }

     public void setDegree(String degree) {
          this.degree = degree;
     }

     public String getExperience() {
          return experience;
     }

     public void setExperience(String experience) {
          this.experience = experience;
     }

     public String getAbout() {
          return about;
     }

     public void setAbout(String about) {
          this.about = about;
     }

     public boolean getAvailable() {
          return available;
     }

     public void setAvailable(boolean available) {
          this.available = available;
     }

     public Long getFees() {
          return fees;
     }

     public void setFees(Long fees) {
          this.fees = fees;
     }

     public Address getAddress() {
          return address;
     }

     public void setAddress(Address address) {
          this.address = address;
     }


     public String getImage() {
          return image;
     }

     public void setImage(String image) {
          this.image = image;
     }


}
