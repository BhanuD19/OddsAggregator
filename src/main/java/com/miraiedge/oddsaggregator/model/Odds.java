package com.miraiedge.oddsaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Odds {
    
    @JsonProperty("eventId")
    private String eventId;
    
    @JsonProperty("market")
    private String market;
    
    @JsonProperty("odds")
    private Double odds;
    
    @JsonProperty("source")
    private String source;
    
    @JsonProperty("updatedTimestamp")
    private LocalDateTime updatedTimestamp;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Odds odds = (Odds) o;
        return Objects.equals(eventId, odds.eventId) && 
               Objects.equals(market, odds.market);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId, market);
    }
    
    public String getCompositeKey() {
        return eventId + "_" + market;
    }
}