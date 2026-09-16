package be.bnpparibasfortis.bookstore.auth.service.impl;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.auth.repository.UserRepository;
import be.bnpparibasfortis.bookstore.auth.service.IAuthService;
import be.bnpparibasfortis.bookstore.auth.service.IJwtService;
import be.bnpparibasfortis.bookstore.exception.InternalServerException;
import be.bnpparibasfortis.bookstore.exception.InvalidEmailOrPasswordException;
import be.bnpparibasfortis.bookstore.exception.EmailAlreadyExistsException;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;
import be.bnpparibasfortis.bookstore.auth.service.models.LoginRequest;
import be.bnpparibasfortis.bookstore.auth.service.models.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, IJwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** TODO:
     *  annotate parameter with the correct annotation to specify that the field cannot be null*/
    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        return Optional.of(request)
                .map(this::toUserEntity)
                .map(userRepository::save)
                .map(jwtService::generateToken)
                .orElseThrow(InternalServerException::new);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return userRepository.findByEmail(request.email())
                .filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
                .map(jwtService::generateToken)
                .orElseThrow(InvalidEmailOrPasswordException::new);
    }

    private UserEntity toUserEntity(RegisterRequest request) {
        return new UserEntity(
                request.email(),
                request.firstName(),
                request.lastName(),
                passwordEncoder.encode(request.password())
        );
    }
}
