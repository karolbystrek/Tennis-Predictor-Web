# Tennis Predictor Web Application

## Description

This web application provides a user interface for interacting with the Tennis Predictor API. Users can input player details and match conditions to receive tennis match predictions. This application acts as the client-side interface, sending requests to the backend prediction service.

## Prerequisites

Before you begin, ensure you have the following installed:
*   **Java Development Kit (JDK):** Version 21 or higher. You can check your version using `java -version`.
*   **Apache Maven:** Version 3.6 or higher. You can check your version using `mvn -version`.
*   **Tennis Predictor API:** The backend service (`tennis-predictor-api`) must be running and accessible from this application.

## Configuration

Application configuration is managed in the `src/main/resources/application.properties` file. Key settings include:

*   **Server Port:**
    ```properties
    server.port=8080 # Or your desired port
    ```
*   **Tennis Predictor API URL:** You need to configure the base URL of the running `tennis-predictor-api` service. Add a property like this:
    ```properties
    # Example: Replace with the actual URL where your API is running
    tennis.predictor.api.base-url=http://localhost:8081/api
    ```
    *(Note: You will need to update the application code, likely in the `PredictionService` or a dedicated API client class, to read and use this property when making requests to the backend API.)*

*   **Database Configuration (if applicable):** If this web application uses its own database (e.g., for user accounts), configure the datasource properties:
    ```properties
    # Example for MySQL
    # spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
    # spring.datasource.username=your_db_user
    # spring.datasource.password=your_db_password
    # spring.jpa.hibernate.ddl-auto=update # Or validate, none, etc.
    ```

*   **Security Settings:** Configure security-related properties as needed.

## Running the Application

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd tennis-predictor-web
    ```
2.  **Ensure the Tennis Predictor API is running.**
3.  **Build the project using Maven:**
    ```bash
    ./mvnw clean package
    # On Windows, use: mvnw.cmd clean package
    ```
    This command cleans the project, compiles the code, runs tests, and packages the application into a JAR file located in the `target/` directory.
4.  **Run the application:**
    ```bash
    java -jar target/TennisPredictor-1.0.jar
    # Replace TennisPredictor-1.0.jar with the actual JAR file name
    ```
    Alternatively, you can run directly using the Maven Spring Boot plugin (useful for development):
    ```bash
    ./mvnw spring-boot:run
    # On Windows, use: mvnw.cmd spring-boot:run
    ```
5.  **Access the application:** Open your web browser and navigate to `http://localhost:8080` (or the configured `server.port`).

## Technology Stack

*   **Java:** Version 21
*   **Spring Boot:** Framework for building the web application.
*   **Spring Web:** For handling web requests.
*   **Spring Security:** For authentication and authorization (if used).
*   **Thymeleaf:** Server-side Java template engine for the frontend UI.
*   **Maven:** Build automation and dependency management.
*   **(Optional) Spring Data JPA:** For database interaction (if applicable).

## Contributing

Please refer to the contribution guidelines if you wish to contribute to this project. (You can add a `CONTRIBUTING.md` file later).

## License

Specify your project's license here (e.g., MIT, Apache 2.0). (You can add a `LICENSE` file later).
