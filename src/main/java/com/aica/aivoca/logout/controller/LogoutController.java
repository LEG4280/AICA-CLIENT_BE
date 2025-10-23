package com.aica.aivoca.logout.controller;

import com.aica.aivoca.global.exception.dto.SuccessStatusResponse;
import com.aica.aivoca.global.exception.message.SuccessMessage;
import com.aica.aivoca.logout.service.LogoutService;
import com.aica.aivoca.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<SuccessStatusResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logoutService.logout(userDetails.userId());
        return ResponseEntity.ok(SuccessStatusResponse.of(SuccessMessage.USER_LOGOUT_SUCCESS));
    }

}