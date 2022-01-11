package ru.ygreens.blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.ygreens.blog.dto.JwtResponse;
import ru.ygreens.blog.dto.LoginRequest;
import ru.ygreens.blog.dto.MessageResponse;
import ru.ygreens.blog.dto.SignupRequest;
import ru.ygreens.blog.models.ERole;
import ru.ygreens.blog.models.Role;
import ru.ygreens.blog.models.User;
import ru.ygreens.blog.repository.RoleRepository;
import ru.ygreens.blog.repository.UserRepository;
import ru.ygreens.blog.security.JwtUtil;
import ru.ygreens.blog.service.UserDetailsImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("signin")
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                jwtToken,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
                ));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is exist"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is exist"));
        }

        if (userRepository.existsByEmail(signupRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone is exist"));
        }

        if (!signupRequest.getPassword().equals(signupRequest.getPasswordTwo())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password mismatch"));
        }

        Role role = roleRepository.findByName(ERole.ROLE_USER)
                .orElse(roleRepository.save(new Role(ERole.ROLE_USER)));

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPhone(),
                passwordEncoder.encode(signupRequest.getPassword())
        );
        user.setRoles(Set.of(role));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User CREATED"));
    }
}
