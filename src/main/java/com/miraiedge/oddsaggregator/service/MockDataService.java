package com.miraiedge.oddsaggregator.service;

import com.miraiedge.oddsaggregator.model.Odds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class MockDataService {
    
    private final Random random = new Random();
    
    private final String[] events = {
        "MATCH_001", "MATCH_002", "MATCH_003", "MATCH_004", "MATCH_005"
    };
    
    private final String[] markets = {
        "MATCH_WINNER", "OVER_UNDER_2_5", "BOTH_TEAMS_TO_SCORE", "HANDICAP"
    };
    
    public List<Odds> generateMockOdds(String source) {
        List<Odds> oddsList = new ArrayList<>();
        int numberOfEvents = 2 + random.nextInt(4); // 2-5 events
        
        for (int i = 0; i < numberOfEvents; i++) {
            String eventId = events[random.nextInt(events.length)];
            String market = markets[random.nextInt(markets.length)];
            double oddsValue = 1.5 + (random.nextDouble() * 8.5); // 1.5 to 10.0
            
            Odds odds = Odds.builder()
                    .eventId(eventId)
                    .market(market)
                    .odds(Math.round(oddsValue * 100.0) / 100.0) // Round to 2 decimal places, using Math.round() instead of Bigdecimal with set scale.
                    .source(source)
                    .updatedTimestamp(LocalDateTime.now())
                    .build();
            
            oddsList.add(odds);
        }
        
        log.debug("Generated {} mock odds for source: {}", oddsList.size(), source);
        return oddsList;
    }
}