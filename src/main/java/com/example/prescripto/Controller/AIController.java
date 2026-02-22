package com.example.prescripto.Controller;


import com.example.prescripto.DTO.DoctorAboutDTO;
import com.example.prescripto.Service.Implementation.OllamaServiceImpl;
import com.example.prescripto.Utils.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
public class AIController {


      @Autowired
      private OllamaServiceImpl ollamaService;


    @GetMapping("/api/test")
    public String test() {
        System.out.println("TEST ENDPOINT HIT");
        return "ok";
    }


       @PostMapping("/api/ai/generate-doctor-about")
       public APIResponse generateDoctorAbout(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody DoctorAboutDTO doctorAboutDTO){

           String about= ollamaService.getAbout(doctorAboutDTO);

           return new APIResponse(true, about);
       }


       @PostMapping("/api/ai/chat")
       public APIResponse chatWithAI(@RequestHeader("Authorization") String authHeader,
                                      @RequestBody String question) throws IOException {

           System.out.println("Received question: " + question);

           String response = ollamaService.chat(question);


           return new APIResponse(true, response);

       }

}
