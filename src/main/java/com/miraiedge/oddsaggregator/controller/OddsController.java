package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.OddsAggregatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Odds Aggregation", description = "Endpoints for retrieving aggregated odds data from multiple sources")
public class OddsController {
    
    private final OddsAggregatorService oddsAggregatorService;
    
    @Operation(
        summary = "Get current aggregated odds",
        description = "Retrieves the current aggregated odds from all configured sources. Returns a list of odds data for various events and markets."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved current odds",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Odds.class))
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while retrieving odds"
        )
    })
    @GetMapping
    public ResponseEntity<List<Odds>> getCurrentOdds() {
        log.debug("Request received for current odds");
        List<Odds> odds = oddsAggregatorService.getCurrentOdds();
        log.debug("Returning {} odds", odds.size());
        return ResponseEntity.ok(odds);
    }

    @Operation(
        summary = "Stream live odds updates",
        description = "Establishes a Server-Sent Events (SSE) connection to receive real-time odds updates. The connection will continuously stream odds changes as they occur."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "SSE connection established successfully",
            content = @Content(
                mediaType = "text/event-stream",
                schema = @Schema(implementation = Odds.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Failed to establish SSE connection"
        )
    })
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOdds() {
        log.debug("New SSE connection requested");
        return oddsAggregatorService.addSseEmitter();
    }

    @Operation(
        summary = "Get service health status",
        description = "Returns the health status of the odds aggregation service, including the number of active odds and SSE connections."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Health status retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class)
            )
        )
    })
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