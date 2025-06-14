package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.OddsAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/odds")
@RequiredArgsConstructor
@Slf4j
public class OddsController {
    
    private final OddsAggregatorService oddsAggregatorService;
    
    @GetMapping
    public ResponseEntity<List<Odds>> getCurrentOdds() {
        log.debug("Request received for current odds");
        List<Odds> odds = oddsAggregatorService.getCurrentOdds();
        log.debug("Returning {} odds", odds.size());
        return ResponseEntity.ok(odds);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOdds() {
        log.debug("New SSE connection requested");
        return oddsAggregatorService.addSseEmitter();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("activeOdds", oddsAggregatorService.getCurrentOdds().size());
        health.put("activeSseConnections", oddsAggregatorService.getActiveSseConnections());

        return ResponseEntity.ok(health);
    }
}