package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.MockDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockSourceController.class)
class MockSourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MockDataService mockDataService;

    @Test
    void testGetSource1Odds_ReturnsEmptyList() throws Exception {
        // Arrange
        Mockito.when(mockDataService.generateMockOdds("SOURCE_1")).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/mock/source1/odds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetSource1Odds_ReturnsListWithOdds() throws Exception {
        // Arrange
        List<Odds> oddsList = new ArrayList<>();
        oddsList.add(Odds.builder()
                .eventId("MATCH_001")
                .market("MATCH_WINNER")
                .odds(1.5)
                .source("SOURCE_1")
                .updatedTimestamp(LocalDateTime.now())
                .build());
        oddsList.add(Odds.builder()
                .eventId("MATCH_002")
                .market("OVER_UNDER_2_5")
                .odds(2.5)
                .source("SOURCE_1")
                .updatedTimestamp(LocalDateTime.now())
                .build());

        Mockito.when(mockDataService.generateMockOdds("SOURCE_1")).thenReturn(oddsList);

        // Act & Assert
        mockMvc.perform(get("/mock/source1/odds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId", is("MATCH_001")))
                .andExpect(jsonPath("$[0].market", is("MATCH_WINNER")))
                .andExpect(jsonPath("$[0].odds", is(1.5)))
                .andExpect(jsonPath("$[0].source", is("SOURCE_1")))
                .andExpect(jsonPath("$[1].eventId", is("MATCH_002")))
                .andExpect(jsonPath("$[1].market", is("OVER_UNDER_2_5")))
                .andExpect(jsonPath("$[1].odds", is(2.5)))
                .andExpect(jsonPath("$[1].source", is("SOURCE_1")));
    }

    @Test
    void testGetSource2Odds_ReturnsListWithOdds() throws Exception {
        // Arrange
        List<Odds> oddsList = new ArrayList<>();
        oddsList.add(Odds.builder()
                .eventId("MATCH_003")
                .market("HANDICAP")
                .odds(1.85)
                .source("SOURCE_2")
                .updatedTimestamp(LocalDateTime.now())
                .build());

        Mockito.when(mockDataService.generateMockOdds("SOURCE_2")).thenReturn(oddsList);

        // Act & Assert
        mockMvc.perform(get("/mock/source2/odds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventId", is("MATCH_003")))
                .andExpect(jsonPath("$[0].market", is("HANDICAP")))
                .andExpect(jsonPath("$[0].odds", is(1.85)))
                .andExpect(jsonPath("$[0].source", is("SOURCE_2")));
    }

    @Test
    void testGetSource1Odds_InternalServerError() throws Exception {
        // Arrange
        Mockito.when(mockDataService.generateMockOdds(anyString()))
                .thenThrow(new RuntimeException("Unexpected Error"));

        // Act & Assert
        mockMvc.perform(get("/mock/source1/odds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}