package com.aica.aivoca.wordinfo.controller;

import com.aica.aivoca.global.exception.dto.SuccessStatusResponse;
import com.aica.aivoca.wordinfo.dto.WordInfoDto;
import com.aica.aivoca.wordinfo.service.WordinfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wordinfo")
@RequiredArgsConstructor
public class WordinfoController {

    private final WordinfoService WordinfoService;

    @GetMapping
    public ResponseEntity<SuccessStatusResponse<List<WordInfoDto>>> lookupWord(@RequestParam String word) {
        return ResponseEntity.ok(
                WordinfoService.lookupAndSaveWordIfNeeded(word)
        );
    }
}