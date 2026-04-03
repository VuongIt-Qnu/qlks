package com.example.hotel.service;

import com.example.hotel.dto.user.UserDto;
import com.example.hotel.entity.Role;
import com.example.hotel.entity.User;
import com.example.hotel.repository.RoleRepository;
import com.example.hotel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto createUser(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setActive(dto.isActive());
        Role role = roleRepository.findByName(dto.getRole()).orElseThrow();
        user.setRole(role);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setActive(dto.isActive());
        if (dto.getRole() != null) {
            Role role = roleRepository.findByName(dto.getRole()).orElseThrow();
            user.setRole(role);
        }
        return toDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
