# Odds Aggregator Service

A Spring Boot application that aggregates odds from multiple sources and provides real-time updates via REST API and Server-Sent Events (SSE).

## Features

- **Real-time Odds Aggregation**: Polls multiple sources every 5 seconds
- **REST API**: Get current odds via HTTP endpoint
- **Server-Sent Events**: Real-time streaming of odds updates
- **Thread-Safe**: Uses concurrent data structures for thread safety
- **Mock Data Sources**: Built-in mock endpoints for testing
- **Health Monitoring**: Health check endpoint with system status

## Getting Started

### Prerequisites

- Java 24
- Maven 3.6 or higher

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application.

## Architecture

- **OddsAggregatorService**: Core service that polls sources and manages odds data
- **MockDataService**: Generates random odds data for testing
- **OddsController**: REST API endpoints
- **MockSourceController**: Mock data source endpoints
- **ConcurrentHashMap**: Thread-safe storage for current odds
- **SSE**: Real-time communication with clients

## Configuration

Configuration can be modified in `application.yml`:

- `app.odds.poll-interval`: Polling interval in milliseconds (default: 5000)
- `app.odds.mock-sources`: List of mock source URLs

## Testing

Run tests with: mvn test