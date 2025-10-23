package com.aica.aivoca.auth.controller;

import com.aica.aivoca.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class IdAuthController {

    private final UsersRepository usersRepository;

    @GetMapping("/check-uid")
    public ResponseEntity<Void> checkUserUidDuplicate(@RequestParam String userUid) {
        boolean exists = usersRepository.existsByUserId(userUid);
        return exists
                ? ResponseEntity.status(HttpStatus.CONFLICT).build()  // 409 중복됨
                : ResponseEntity.ok().build();                        // 200 사용 가능
    }
}