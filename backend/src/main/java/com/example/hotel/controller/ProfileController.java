package com.example.hotel.controller;

import com.example.hotel.dto.user.PasswordChangeRequest;
import com.example.hotel.dto.user.ProfileUpdateRequest;
import com.example.hotel.dto.user.UserDto;
import com.example.hotel.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class ProfileController {

    private final UserProfileService userProfileService;

    public ProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public UserDto me() {
        return userProfileService.getMe();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public UserDto update(@RequestBody ProfileUpdateRequest req) {
        return userProfileService.updateProfile(req);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    public void password(@Valid @RequestBody PasswordChangeRequest req) {
        userProfileService.changePassword(req);
    }
}
