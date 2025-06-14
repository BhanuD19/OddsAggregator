package com.miraiedge.oddsaggregator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Odds data model")
public class Odds {
    
    @JsonProperty("eventId")
    @Schema(description = "Unique identifier for the sporting event", example = "EVENT_123")
    private String eventId;
    
    @JsonProperty("market")
    @Schema(description = "Type of betting market", example = "1X2", allowableValues = {"1X2", "OVER_UNDER", "HANDICAP"})
    private String market;
    
    @JsonProperty("odds")
    @Schema(description = "Odds value for the market", example = "2.50", minimum = "1.0")
    private Double odds;
    
    @JsonProperty("source")
    @Schema(description = "Source of the odds data", example = "SOURCE_1")
    private String source;
    
    @JsonProperty("updatedTimestamp")
    @Schema(description = "Timestamp when the odds were last updated")
    private LocalDateTime updatedTimestamp;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Odds odds1 = (Odds) o;
        return Objects.equals(eventId, odds1.eventId) &&
               Objects.equals(market, odds1.market) &&
               Objects.equals(source, odds1.source);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId, market, source);
    }
    
    @Schema(description = "Composite key combining eventId, market, and source")
    public String getCompositeKey() {
        return eventId + "-" + market + "-" + source;
    }
}