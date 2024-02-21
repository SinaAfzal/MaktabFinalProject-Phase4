package ir.maktabsharif.service.impl;

import ir.maktabsharif.model.EmailConfirmationToken;
import ir.maktabsharif.repository.EmailConfirmationTokenRepository;
import ir.maktabsharif.service.EmailConfirmationTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EmailConfirmationTokenServiceImpl implements EmailConfirmationTokenService {
    private final EmailConfirmationTokenRepository repository;


    public EmailConfirmationTokenServiceImpl(EmailConfirmationTokenRepository repository) {
        this.repository = repository;
    }


    @Override
    @Transactional
    public EmailConfirmationToken save(EmailConfirmationToken token) {
        return repository.save(token);
    }

    @Override
    public Optional<EmailConfirmationToken> findByToken(String token) {
        return repository.findByTokenSerial(token);
    }

    @Override
    @Transactional
    public EmailConfirmationToken confirmToken(String tokenSerial) {
        Optional<EmailConfirmationToken> tokenOptional = findByToken(tokenSerial);
        if (tokenOptional.isEmpty())
            throw new IllegalCallerException("Token is not valid!");
        if (tokenOptional.get().getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalCallerException("Token is expired! please request another verification email!");
        if (tokenOptional.get().getConfirmedAt()!=null)
            throw new IllegalCallerException("Token is one-time use only!");
        repository.updateConfirmedAt(tokenSerial, LocalDateTime.now());
        return tokenOptional.get();
    }
}
