package com.miraiedge.oddsaggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(OddsProperties odds) {

    public AppProperties {
        // Provide default value if null
        if (odds == null) {
            odds = new OddsProperties(5000L, List.of(
                    "http://localhost:8080/mock/source1/odds",
                    "http://localhost:8080/mock/source2/odds"
            ));
        }
    }


    public record OddsProperties(
            Long pollInterval,
            List<String> mockSources
    ) {
        public OddsProperties {
            // Provide defaults if null
            if (pollInterval == null) {
                pollInterval = 5000L;
            }
            if (mockSources == null) {
                mockSources = List.of(
                        "http://localhost:8080/mock/source1/odds",
                        "http://localhost:8080/mock/source2/odds"
                );
            }
        }
    }
}
