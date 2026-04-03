package com.example.hotel.service;

import com.example.hotel.dto.user.PasswordChangeRequest;
import com.example.hotel.dto.user.ProfileUpdateRequest;
import com.example.hotel.dto.user.UserDto;
import com.example.hotel.entity.User;
import com.example.hotel.repository.UserRepository;
import com.example.hotel.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto getMe() {
        Long id = SecurityUtils.getCurrentUserId();
        User u = userRepository.findById(id).orElseThrow();
        return toDto(u);
    }

    @Transactional
    public UserDto updateProfile(ProfileUpdateRequest req) {
        Long id = SecurityUtils.getCurrentUserId();
        User u = userRepository.findById(id).orElseThrow();
        if (req.getDisplayName() != null) {
            u.setDisplayName(req.getDisplayName());
        }
        if (req.getEmail() != null) {
            u.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            u.setPhone(req.getPhone());
        }
        return toDto(userRepository.save(u));
    }

    @Transactional
    public void changePassword(PasswordChangeRequest req) {
        Long id = SecurityUtils.getCurrentUserId();
        User u = userRepository.findById(id).orElseThrow();
        if (!passwordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            throw new IllegalArgumentException("Current password invalid");
        }
        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setActive(Boolean.TRUE.equals(user.getActive()));
        dto.setDisplayName(user.getDisplayName());
        dto.setPhone(user.getPhone());
        return dto;
    }
}
