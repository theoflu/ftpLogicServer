package com.yasu.controller;

import com.yasu.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600,allowedHeaders = "*")
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerController {
    private final ServerService serverService;
    @PostMapping("/createDirectories")
    public ResponseEntity<?> createDirectories(String path){
        return ResponseEntity.ok(serverService.createUserDirectories(path).getStatusCode());
    }

}
