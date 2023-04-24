package com.example.demo.configs.controllers;

import com.example.demo.configs.jwt.JwtUtils;
import com.example.demo.models.ERole;
import com.example.demo.models.Parent;
import com.example.demo.models.Role;
import com.example.demo.pojo.JwtResponse;
import com.example.demo.pojo.LoginRequest;
import com.example.demo.pojo.MessageResponse;
import com.example.demo.pojo.SignupRequest;
import com.example.demo.repository.ParentRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.ParentDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authTeacher(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);


        ParentDetailsImpl parentDetails = (ParentDetailsImpl) authentication.getPrincipal();
        List<String> roles = parentDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                parentDetails.getId(),
                parentDetails.getUsername(),
                parentDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerParent(@RequestBody SignupRequest signupRequest) {

        if (parentRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is exist"));
        }

        if (parentRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is exist"));
        }

        Parent parent = new Parent(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> reqRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (reqRoles == null) {
            Role parentRole = roleRepository
                    .findByName(ERole.ROLE_PARENT)
                    .orElseThrow(() -> new RuntimeException("Error, Role PARENT is not found"));
            roles.add(parentRole);
        } else {
            reqRoles.forEach(r -> {
                switch (r) {
                    case "admin":
                        Role teacherRole = roleRepository
                                .findByName(ERole.ROLE_TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error, Role TEACHER is not found"));
                        roles.add(teacherRole);

                        break;
//                    case "mod":
//                        Role modRole = roleRepository
//                                .findByName(ERole.ROLE_MO)
//                                .orElseThrow(() -> new RuntimeException("Error, Role MODERATOR is not found"));
//                        roles.add(modRole);
//
//                        break;

                    default:
                        Role parentRole = roleRepository
                                .findByName(ERole.ROLE_PARENT)
                                .orElseThrow(() -> new RuntimeException("Error, Role PARENT is not found"));
                        roles.add(parentRole);
                }
            });
        }
        parent.setRoles(roles);
        parentRepository.save(parent);
        return ResponseEntity.ok(new MessageResponse("Parent CREATED"));
    }
}
