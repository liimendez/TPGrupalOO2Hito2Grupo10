package com.grupo10.epicentrogourmet.services.implementation;
import org.springframework.stereotype.Service;
@Service
public class EmailService {
    public void enviarMail(String para, String asunto, String cuerpo) {
        System.out.println("=== MAIL SIMULADO A: " + para + " === " + asunto + " / " + cuerpo);
    }
}