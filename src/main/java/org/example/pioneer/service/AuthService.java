package org.example.pioneer.service;

import lombok.RequiredArgsConstructor;
import org.example.pioneer.model.EmailData;
import org.example.pioneer.model.PhoneData;
import org.example.pioneer.model.User;
import org.example.pioneer.repository.EmailDataRepository;
import org.example.pioneer.repository.PhoneDataRepository;
import org.example.pioneer.repository.UserRepository;
import org.example.pioneer.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailDataRepository emailRepo;
    private final PhoneDataRepository phoneRepo;
    private final UserRepository userRepo;
    private final JwtTokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public String login(String identifier, String password) {
        User user;
        if (identifier.contains("@")) {
            EmailData ed = emailRepo.findByEmail(identifier)
                    .orElseThrow(() -> new BadCredentialsException("Неверные учетные данные"));
            user = ed.getUser();
        } else {
            PhoneData pd = phoneRepo.findByPhone(identifier)
                    .orElseThrow(() -> new BadCredentialsException("Неверные учетные данные"));
            user = pd.getUser();
        }

        if (!user.getPasswordHash().equals(password)) {
            throw new BadCredentialsException("Неверные учетные данные");
        }
        return tokenProvider.createToken(user.getId());
    }
}