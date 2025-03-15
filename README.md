# DivFlow - US Stock Dividend Service

An API service that provides US stock dividend information. This service scrapes dividend data from Yahoo Finance and offers it to users.

## Project Overview
DivFlow provides the following features:
- US stock dividend information
- Company name autocomplete functionality
- Company information management (add/delete)
- User authentication registration, login)

## Technology Stack
- **Backend**: Spring Boot, Java
- **Database**: H2 (in-memory database for development)
- **Cache**: Redis
- **Web Scraping**: Jsoup
- **Containerisation**: Docker
- **Authentication**: JWT (JSON Web Token)
- **ORM**: JPA/Hibernate

## API Specification
### Dividend Information
- `GET /finance/dividend/{companyName}`: Retrieve dividend information by company name

### Company Information
- `GET /company/autocomplete?keyword={keyword}`: Company name autocomplete (max 10 results)
- `GET /company?page={page}&size={size}`: Paginated list of registered companies
- `POST /company`:  Add new company information (based on ticker)
- `DELETE /company/{ticker}`: Delete company information

### Authentication
- `POST /auth/signup`: User registration
- `POST /auth/signin`: Login (issues JWT token)

## Key Features
### Web Scraping
Extracts the following information from the Yahoo Finance website:
- Company information (name, ticker)
- Dividend history (date, amount)

### Caching
Utilises Redis to cache dividend information, enhancing service performance:
- Caching of company dividend information
- Automatic cache removal when a company is deleted

### User Authentication
Provides a user authentication system:
- Encrypted password storage
- JWT-based authentication
- Prevention of duplicate IDs

## Installation and Execution
### Requirements
- Java 17 or higher
- Gradle
- Redis (for caching)
- Docker (optional)

## Running Locally
1. Clone the project
``` bash
git clone https://github.com/faithcoderlab/divflow.git
cd divflow
```
2. Run Redis
``` bash
# Using Docker
docker run -p 6379:6379 --name redis-divflow -d redis

# Or run a local Redis server
redis-server
```
3. Run the application
``` bash
./gradlew bootRun
```
4. Access the API
``` bash
http://localhost:8080
```

## Configuration
You can adjust the following settings in the `application.yml` file:
- Database connection information
- Redis host and port
- JWT secret key and token validity period
- Logging levels

## Logging
The application records logs at various levels:
- Debug level: API request/response information
- Info level: Service operations
- Error level: Application errors and exceptions

## Error Handling
The application uses a global exception handler to manage errors:
- Custom exceptions for specific error scenarios
- Appropriate HTTP status codes for different erros
- Consistent error response format

## Security
- Passwords are encrypted using BCrypt
- Authentication is managed through JWT tokens
- API endpoints are protected based on user roles

## Project Structure
The project follows a standard Spring Boot application structure:
- Controllers: Handle HTTP requests
- Services: Contain business logic
- Repositories: Manage data access
- Models: Define data entities
- DTOs: Data transfer objects for API communication
- Exception handlers: Manage error responses
- Configuration: Application settings

## Future Enhancements
- Enhanced dividend analytics
- Support for additional financial data sources
- User portfolio management
- Dividend payment notifications
- Mobile application support
