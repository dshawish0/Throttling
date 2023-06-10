package com.deiaa.throttling.controller;

import com.deiaa.throttling.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping
    public ResponseEntity<String> getHistory() {
        try {
            return ResponseEntity.ok(historyService.getHistory());
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("503 Service Unavailable");
        }
    }

    public void setHistoryService(HistoryService mockHistoryService) {
        this.historyService = mockHistoryService;
    }
}
