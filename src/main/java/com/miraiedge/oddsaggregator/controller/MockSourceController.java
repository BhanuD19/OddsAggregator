package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.ErrorResponse;
import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.MockDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Mock Data Sources", description = "Endpoints for retrieving mock odds data from simulated sources")
public class MockSourceController {
    
    private final MockDataService mockDataService;
    
    @Operation(
        summary = "Get odds from Source 1",
        description = "Retrieves mock odds data from simulated Source 1. This endpoint provides sample odds data for testing and development purposes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved odds from Source 1",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Odds.class))
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while generating mock data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/source1/odds")
    public ResponseEntity<List<Odds>> getSource1Odds() {
        log.debug("Request received for source1 odds");
        List<Odds> odds = mockDataService.generateMockOdds("SOURCE_1");
        return ResponseEntity.ok(odds);
    }

    @Operation(
        summary = "Get odds from Source 2",
        description = "Retrieves mock odds data from simulated Source 2. This endpoint provides sample odds data for testing and development purposes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved odds from Source 2",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Odds.class))
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error occurred while generating mock data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/source2/odds")
    public ResponseEntity<List<Odds>> getSource2Odds() {
        log.debug("Request received for source2 odds");
        List<Odds> odds = mockDataService.generateMockOdds("SOURCE_2");
        return ResponseEntity.ok(odds);
    }
}