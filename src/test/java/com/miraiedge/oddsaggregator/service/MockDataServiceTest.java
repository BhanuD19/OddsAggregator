package com.miraiedge.oddsaggregator.service;

import com.miraiedge.oddsaggregator.model.Odds;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MockDataServiceTest {

    private final MockDataService mockDataService = new MockDataService();

    /**
     * Test to verify that generateMockOdds creates a list of odds with the correct number of entries and populated fields.
     */
    @Test
    void shouldGenerateMockOddsWithValidNumberOfEntriesAndPopulatedFields() {
        // Arrange
        String source = "TestSource";

        // Act
        List<Odds> oddsList = mockDataService.generateMockOdds(source);

        // Assert
        assertThat(oddsList).isNotEmpty();
        assertThat(oddsList.size()).isBetween(2, 5);
        for (Odds odds : oddsList) {
            assertThat(odds.getEventId()).isNotNull();
            assertThat(odds.getMarket()).isNotNull();
            assertThat(odds.getOdds()).isBetween(1.5, 10.0);
            assertThat(odds.getSource()).isEqualTo(source);
            assertThat(odds.getUpdatedTimestamp()).isNotNull();
        }
    }

    /**
     * Test to ensure that generateMockOdds produces unique objects for each call.
     */
    @Test
    void shouldGenerateUniqueOddsForEachInvocation() {
        // Arrange
        String source = "TestSource";

        // Act
        List<Odds> oddsList1 = mockDataService.generateMockOdds(source);
        List<Odds> oddsList2 = mockDataService.generateMockOdds(source);

        // Assert
        assertThat(oddsList1).isNotEqualTo(oddsList2);
        assertThat(oddsList1).doesNotContainAnyElementsOf(oddsList2);
    }

    /**
     * Test to verify that generateMockOdds handles empty or null source values appropriately.
     */
    @Test
    void shouldHandleNullOrEmptySource() {
        // Act
        List<Odds> oddsListWithNullSource = mockDataService.generateMockOdds(null);
        List<Odds> oddsListWithEmptySource = mockDataService.generateMockOdds("");

        // Assert
        assertThat(oddsListWithNullSource).isNotEmpty();
        for (Odds odds : oddsListWithNullSource) {
            assertThat(odds.getSource()).isNull();
        }

        assertThat(oddsListWithEmptySource).isNotEmpty();
        for (Odds odds : oddsListWithEmptySource) {
            assertThat(odds.getSource()).isEmpty();
        }
    }
}