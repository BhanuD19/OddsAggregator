package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.MockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
@Slf4j
public class MockSourceController {
    private final MockDataService mockDataService;

    @GetMapping("/source1/odds")
    public ResponseEntity<List<Odds>> getSource1Odds() {
        log.debug("Request received for source1 odds");
        List<Odds> odds = mockDataService.generateMockOdds("SOURCE_1");
        return ResponseEntity.ok(odds);
    }

    @GetMapping("/source2/odds")
    public ResponseEntity<List<Odds>> getSource2Odds() {
        log.debug("Request received for source2 odds");
        List<Odds> odds = mockDataService.generateMockOdds("SOURCE_2");
        return ResponseEntity.ok(odds);
    }
}