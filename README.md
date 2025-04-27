# Tennis Predictor Web Application

## Description

This web application provides a user interface for interacting with the Tennis Predictor API. Users can input player details and match conditions to receive tennis match predictions. This application acts as the client-side interface, sending requests to the backend prediction service. It includes features like user registration, login, and a dynamic player search interface.

## Prerequisites

Before you begin, ensure you have the following installed:

* **Java Development Kit (JDK):** Version 21 or higher. You can check your version using `java -version`.
* **Apache Maven:** Version 3.6 or higher. You can check your version using `mvn -version`.
* **MySQL Database:** A running instance accessible by the application.
* **Tennis Predictor API:** The backend service (`tennis-predictor-api`) must be running and accessible from this application.

## Configuration

Application configuration is managed in the `src/main/resources/application.properties` file and profile-specific files like `application-dev.properties`. Key settings include:

* **Server Port:**

    ```properties
    server.port=8080 # Or your desired port
    ```

* **Spring Profile:**

    ```properties
    spring.profiles.active=dev # e.g., dev, prod
    ```

* **Tennis Predictor API Configuration:** Configure the details of the running `tennis-predictor-api` service.

    ```properties
    # Base URL of the prediction API
    tennis.predictor.api.base-url=http://127.0.0.1:5000

    # API Key for authentication
    # It's recommended to use environment variables for sensitive keys
    tennis.predictor.api.key=${PREDICTION_API_KEY}

    # Specific path for the prediction endpoint
    tennis.predictor.api.predict-path=/predict
    ```

* **Database Configuration:** Configure the datasource properties for user accounts and other application data.

    ```properties
    # Example for MySQL (values shown are examples from application.properties)
    spring.datasource.url=jdbc:mysql://localhost:3306/tennis
    spring.datasource.username=${MYSQL_USER}       # Use environment variable
    spring.datasource.password=${MYSQL_PASSWORD}   # Use environment variable
    spring.jpa.hibernate.ddl-auto=update           # Or validate, none, etc.
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```

* **Security Settings:** Configure security-related properties as needed within the Spring Security configuration classes.

## Running the Application

1. **Clone the repository:**

    ```bash
    git clone <your-repository-url>
    cd tennis-predictor-web
    ```

2. **Ensure the Tennis Predictor API is running.**
3. **Ensure your MySQL database is running.**
4. **Set Environment Variables:** Ensure `MYSQL_USER`, `MYSQL_PASSWORD`, and `PREDICTION_API_KEY` are set in your environment.

    ```bash
    export MYSQL_USER=your_db_user
    export MYSQL_PASSWORD=your_db_password
    export PREDICTION_API_KEY=your_api_key
    ```

5. **Build the project using Maven:**

    ```bash
    ./mvnw clean package
    # On Windows, use: mvnw.cmd clean package
    ```

6. **Run the application:**

    ```bash
    java -jar tennis-predictor-web-1.0.jar
    ```

    Alternatively, you can run directly using the Maven Spring Boot plugin (useful for development):

    ```bash
    ./mvnw spring-boot:run
    # On Windows, use: mvnw.cmd spring-boot:run
    ```

7. **Access the application:** Open your web browser and navigate to [http://localhost:8080](http://localhost:8080) (or the configured `server.port`).

## Technology Stack

* **Backend:**
  * Java 21
  * Spring Boot 3.4.4
  * Spring Web
  * Spring Security
  * Spring Data JPA
  * Spring WebFlux
  * Lombok
  * Hibernate Validator
* **Frontend:**
  * Thymeleaf
  * JavaScript
* **Database:**
  * MySQL
* **Build & Dependency Management:**
  * Apache Maven
* **Testing:**
  * JUnit 5
  * Mockito
  * Spring Boot Test
  * Spring Security Test
  * MockWebServer (for testing HTTP clients)

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.
