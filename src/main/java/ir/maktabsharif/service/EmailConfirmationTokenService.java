package ir.maktabsharif.service;

import ir.maktabsharif.model.EmailConfirmationToken;

import java.util.Optional;


public interface EmailConfirmationTokenService {
    EmailConfirmationToken save(EmailConfirmationToken token);
    Optional<EmailConfirmationToken> findByToken(String token);
    EmailConfirmationToken confirmToken(String token);
}
