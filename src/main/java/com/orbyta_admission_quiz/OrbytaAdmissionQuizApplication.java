package com.orbyta_admission_quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class OrbytaAdmissionQuizApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrbytaAdmissionQuizApplication.class, args);
    }
}
