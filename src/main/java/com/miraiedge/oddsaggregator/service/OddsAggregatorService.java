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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class OddsAggregatorService {
    
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    private final ConcurrentHashMap<String, Odds> currentOdds = new ConcurrentHashMap<>();

    private final CopyOnWriteArrayList<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();
    private final List<String> mockSources;

    public OddsAggregatorService(RestTemplate restTemplate, AppProperties appProperties) {
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
        this.mockSources = List.copyOf(appProperties.odds().mockSources());
    }

    @Scheduled(fixedRate = 5000)
    public void aggregateOdds() {
        log.debug("Starting odds aggregation cycle");
        
        List<Odds> allNewOdds = new ArrayList<>();
        
        for (String sourceUrl : mockSources) {
            try {
                List<Odds> sourceOdds = fetchOddsFromSource(sourceUrl);
                allNewOdds.addAll(sourceOdds);
                log.debug("Fetched {} odds from source: {}", sourceOdds.size(), sourceUrl);
            } catch (Exception e) {
                log.error("Failed to fetch odds from source: {}", sourceUrl, e);
            }
        }
        
        // Process and deduplicate odds
        boolean hasUpdates = processOdds(allNewOdds);

        // Notify SSE clients if there are updates
        if (hasUpdates) {
            notifySseClients();
        }

        log.debug("Odds aggregation cycle completed. Current odds count: {}", currentOdds.size());
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

    public SseEmitter addSseEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> {
            sseEmitters.remove(emitter);
            log.debug("SSE emitter removed on completion. Active emitters: {}", sseEmitters.size());
        });

        emitter.onTimeout(() -> {
            sseEmitters.remove(emitter);
            log.debug("SSE emitter removed on timeout. Active emitters: {}", sseEmitters.size());
        });

        emitter.onError((throwable) -> {
            sseEmitters.remove(emitter);
            log.error("SSE emitter error. Active emitters: {}", sseEmitters.size(), throwable);
        });

        sseEmitters.add(emitter);
        log.debug("New SSE emitter added. Active emitters: {}", sseEmitters.size());

        // Send current odds to new subscriber
        try {
            emitter.send(SseEmitter.event()
                    .name("odds-update")
                    .data(getCurrentOdds()));
        } catch (Exception e) {
            log.error("Failed to send initial odds to new SSE subscriber", e);
            sseEmitters.remove(emitter);
        }

        return emitter;
    }

    private void notifySseClients() {
        if (sseEmitters.isEmpty()) {
            return;
        }

        List<Odds> currentOddsList = getCurrentOdds();
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : sseEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("odds-update")
                        .data(currentOddsList));
            } catch (Exception e) {
                log.error("Failed to send SSE update to client", e);
                deadEmitters.add(emitter);
            }
        }

        // Remove dead emitters
        sseEmitters.removeAll(deadEmitters);

        if (!deadEmitters.isEmpty()) {
            log.debug("Removed {} dead SSE emitters. Active emitters: {}",
                    deadEmitters.size(), sseEmitters.size());
        }
    }

    public int getActiveSseConnections() {
        return sseEmitters.size();
    }
}