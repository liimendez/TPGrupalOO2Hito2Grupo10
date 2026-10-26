package com.grupo10.epicentrogourmet.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void enviarMail(String to, String asunto, String cuerpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(asunto);
            message.setText(cuerpo);
            mailSender.send(message);
            System.out.println(">>> MAIL REAL ENVIADO A " + to);
        } catch (Exception e) {
            System.err.println("ERROR MAIL REAL: " + e.getMessage());
            System.out.println("=== MAIL SIMULADO FALLBACK A: " + to + " === " + cuerpo);
        }
    }
}