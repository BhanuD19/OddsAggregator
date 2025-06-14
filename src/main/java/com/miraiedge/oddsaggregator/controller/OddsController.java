package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.OddsAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}