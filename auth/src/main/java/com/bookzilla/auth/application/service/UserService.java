package com.bookzilla.auth.application.service;

import com.bookzilla.auth.application.exception.EmailAlreadyRegisteredException;
import com.bookzilla.auth.application.port.out.ProviderRepository;
import com.bookzilla.auth.application.port.out.UserRepository;
import com.bookzilla.auth.domain.Provider;
import com.bookzilla.auth.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {


    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder encoder;


    @Autowired
    public UserService(UserRepository userRepository, ProviderRepository providerRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.encoder = encoder;
    }


    public void register(String firstName,
                         String lastName,
                         String password,
                         String email) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("Email is already registered!");
        }

        User user = new User(firstName, lastName, encoder.encode(password), email);

        User savedUser = userRepository.save(user);

        Provider provider = new Provider(savedUser);

        providerRepository.save(provider);
    }
}
