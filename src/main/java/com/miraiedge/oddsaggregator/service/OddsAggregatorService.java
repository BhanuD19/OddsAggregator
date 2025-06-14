package com.miraiedge.oddsaggregator.service;

import com.miraiedge.oddsaggregator.config.AppProperties;
import com.miraiedge.oddsaggregator.model.Odds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OddsAggregatorService {
    
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    private final ConcurrentHashMap<String, Odds> currentOdds = new ConcurrentHashMap<>();

    @Scheduled(fixedRateString = "#{@com.miraiedge.oddsaggregator.appProperties.odds().pollInterval()}")
    public void aggregateOdds() {
        log.debug("Starting odds aggregation cycle");
        
        List<Odds> allNewOdds = new ArrayList<>();
        
        for (String sourceUrl : appProperties.odds().mockSources()) {
            try {
                List<Odds> sourceOdds = fetchOddsFromSource(sourceUrl);
                allNewOdds.addAll(sourceOdds);
                log.debug("Fetched {} odds from source: {}", sourceOdds.size(), sourceUrl);
            } catch (Exception e) {
                log.error("Failed to fetch odds from source: {}", sourceUrl, e);
            }
        }
        
        // Process and deduplicate odds
        processOdds(allNewOdds);
    }
    
    private List<Odds> fetchOddsFromSource(String sourceUrl) {
        try {
            ResponseEntity<List<Odds>> response = restTemplate.exchange(
                    sourceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Odds>>() {}
            );
            
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching odds from source: {}", sourceUrl, e);
            return new ArrayList<>();
        }
    }
    
    private boolean processOdds(List<Odds> newOdds) {
        boolean hasUpdates = false;
        
        for (Odds odds : newOdds) {
            String key = odds.getCompositeKey();
            Odds existingOdds = currentOdds.get(key);
            
            // Update if new odds or if timestamp is newer
            if (existingOdds == null || 
                odds.getUpdatedTimestamp().isAfter(existingOdds.getUpdatedTimestamp())) {
                currentOdds.put(key, odds);
                hasUpdates = true;
                log.debug("Updated odds for key: {} with odds: {}", key, odds.getOdds());
            }
        }
        
        return hasUpdates;
    }
    
    public List<Odds> getCurrentOdds() {
        return new ArrayList<>(currentOdds.values());
    }
}