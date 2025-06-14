package com.miraiedge.oddsaggregator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response structure")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "500")
    private int status;
    
    @Schema(description = "Error message", example = "Internal server error")
    private String message;
    
    @Schema(description = "Detailed error description", example = "Failed to generate mock data")
    private String details;
    
    @Schema(description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request path where the error occurred", example = "/mock/source1/odds")
    private String path;
}