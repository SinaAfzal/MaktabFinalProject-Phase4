package ir.maktabsharif.service;

import ir.maktabsharif.model.Email;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendWithHTMLTemplate(Email email) throws MessagingException;
}
