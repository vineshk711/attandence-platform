# AI-Enhanced Attendance Operations Platform

A modular platform that handles employee attendance via APIs and integrates an AI assistant for generating insights and summaries.

## Features

- REST APIs for managing employee attendance
- AI-powered insights using OpenAI's GPT-4o-mini model
- Dashboard for attendance analytics
- Interactive AI assistant
- Containerized for easy deployment

## Technologies Used

- Java 17
- Spring Boot 3.2.2
- PostgreSQL
- Thymeleaf for UI
- LangChain4j for AI integration
- Docker for containerization

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven
- PostgreSQL (or use Docker)
- OpenAI API key

### Configuration

1. Add your OpenAI API key to `src/main/resources/application.properties`:
   ```
   openai.api.key=your-openai-api-key
   ```

2. Configure the database connection in `application.properties` if not using the default settings.

### Running the Application

#### Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

#### Using Docker Compose

```bash
# Set your OpenAI API key as an environment variable
export OPENAI_API_KEY=your-openai-api-key

# Start the containers
docker compose up -d
```

The application will be available at http://localhost:8080

> **Note:** The Docker setup uses OpenJDK 17 images for better compatibility across different platforms.

## API Endpoints

### Employee APIs
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create a new employee
- `PUT /api/employees/{id}` - Update an employee
- `DELETE /api/employees/{id}` - Delete an employee

### Attendance APIs
- `GET /api/attendances` - Get all attendance records
- `GET /api/attendances/{id}` - Get attendance by ID
- `POST /api/attendances` - Create a new attendance record
- `PUT /api/attendances/{id}` - Update an attendance record
- `DELETE /api/attendances/{id}` - Delete an attendance record

### Analytics APIs
- `GET /api/attendances/analytics/absences/{employeeId}/{month}/{year}` - Count absences for an employee in a month
- `GET /api/attendances/analytics/absences/{month}/{year}` - Get employees with most absences in a month
- `GET /api/attendances/analytics/remote-work/{month}/{year}` - Get employees with most remote days in a month

### AI Insight APIs
- `GET /api/insights/daily/{date}` - Get AI-generated daily attendance summary
- `GET /api/insights/weekly?startDate={startDate}&endDate={endDate}` - Get weekly attendance summary
- `GET /api/insights/absences/{month}/{year}` - Get insight on most absent employees
- `GET /api/insights/remote-work/{month}/{year}` - Get insight on remote work patterns
- `POST /api/insights/query` - Ask a natural language question about attendance 