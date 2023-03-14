package com.team29.backend.ip;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/get-ip")
public class RequestServiceController {
    @Autowired
    private RequestService requestService;
    @GetMapping
    public ResponseEntity<String> getUserIp(HttpServletRequest request) {
        String clientIp = requestService.getClientIp(request);
        return ResponseEntity.ok(clientIp);
    }
    
}
