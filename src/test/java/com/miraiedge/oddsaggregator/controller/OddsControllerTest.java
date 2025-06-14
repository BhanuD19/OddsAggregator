package com.miraiedge.oddsaggregator.controller;

import com.miraiedge.oddsaggregator.model.Odds;
import com.miraiedge.oddsaggregator.service.OddsAggregatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OddsController.class)
class OddsControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OddsAggregatorService oddsAggregatorService;


    @Test
    void testGetCurrentOdds() throws Exception {
        List<Odds> mockOdds = Collections.singletonList(
                Odds.builder()
                        .eventId("TEST_001")
                        .market("MATCH_WINNER")
                        .odds(2.50)
                        .source("TEST_SOURCE")
                        .updatedTimestamp(LocalDateTime.now())
                        .build()
        );

        when(oddsAggregatorService.getCurrentOdds()).thenReturn(mockOdds);

        mockMvc.perform(get("/odds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].eventId").value("TEST_001"))
                .andExpect(jsonPath("$[0].market").value("MATCH_WINNER"))
                .andExpect(jsonPath("$[0].odds").value(2.50))
                .andExpect(jsonPath("$[0].source").value("TEST_SOURCE"));
    }

    @Test
    void testGetCurrentOdds_EmptyList() throws Exception {
        when(oddsAggregatorService.getCurrentOdds()).thenReturn(List.of());

        mockMvc.perform(get("/odds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetCurrentOdds_MultipleOdds() throws Exception {
        List<Odds> mockOdds = Arrays.asList(
                Odds.builder()
                        .eventId("TEST_001")
                        .market("MATCH_WINNER")
                        .odds(2.50)
                        .source("SOURCE_1")
                        .updatedTimestamp(LocalDateTime.now())
                        .build(),
                Odds.builder()
                        .eventId("TEST_002")
                        .market("OVER_UNDER_2_5")
                        .odds(1.85)
                        .source("SOURCE_2")
                        .updatedTimestamp(LocalDateTime.now())
                        .build()
        );

        when(oddsAggregatorService.getCurrentOdds()).thenReturn(mockOdds);

        mockMvc.perform(get("/odds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].eventId").value("TEST_001"))
                .andExpect(jsonPath("$[1].eventId").value("TEST_002"));
    }

}