package com.miraiedge.oddsaggregator.service;

import com.miraiedge.oddsaggregator.config.AppProperties;
import com.miraiedge.oddsaggregator.model.Odds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OddsAggregatorServiceTest {
    
    @Mock
    private RestTemplate restTemplate;
    
    private AppProperties appProperties;
    private OddsAggregatorService oddsAggregatorService;
    
    @BeforeEach
    void setUp() {
        // Create test configuration using records
        var oddsProperties = new AppProperties.OddsProperties(
            1000L,
            List.of(
                "http://localhost:8080/mock/source1/odds",
                "http://localhost:8080/mock/source2/odds"
            )
        );
        appProperties = new AppProperties(oddsProperties);
        
        oddsAggregatorService = new OddsAggregatorService(restTemplate, appProperties);
    }
    
    @Test
    void testGetCurrentOdds_InitiallyEmpty() {
        List<Odds> odds = oddsAggregatorService.getCurrentOdds();
        assertNotNull(odds);
        assertTrue(odds.isEmpty());
    }
    
    @Test
    void testAddSseEmitter_ReturnsEmitter() {
        var emitter = oddsAggregatorService.addSseEmitter();
        assertNotNull(emitter);
        assertEquals(1, oddsAggregatorService.getActiveSseConnections());
    }
    
    @Test
    void testGetActiveSseConnections_InitiallyZero() {
        assertEquals(0, oddsAggregatorService.getActiveSseConnections());
    }
    
    @Test
    void testAddMultipleSseEmitters() {
        var emitter1 = oddsAggregatorService.addSseEmitter();
        var emitter2 = oddsAggregatorService.addSseEmitter();
        
        assertNotNull(emitter1);
        assertNotNull(emitter2);
        assertEquals(2, oddsAggregatorService.getActiveSseConnections());
    }
    
    @Test
    void testAppPropertiesConfiguration() {
        assertEquals(1000L, appProperties.odds().pollInterval());
        assertEquals(2, appProperties.odds().mockSources().size());
        assertTrue(appProperties.odds().mockSources().contains("http://localhost:8080/mock/source1/odds"));
        assertTrue(appProperties.odds().mockSources().contains("http://localhost:8080/mock/source2/odds"));
    }
}