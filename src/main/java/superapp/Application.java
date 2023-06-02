package superapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of the application.
 * <p>
 * This class is annotated with the `@SpringBootApplication` annotation, which combines three commonly used annotations:
 * - `@Configuration`: Indicates that this class serves as a configuration source for the application context.
 * - `@EnableAutoConfiguration`: Enables auto configuration of the Spring application context.
 * - `@ComponentScan`: Enables component scanning for the application to automatically discover and register beans.
 * <p>
 * The `Application` class provides the entry point for the application by defining a `main` method. When the `main` method
 * is executed, it starts the Spring Boot application by calling `SpringApplication.run(Application.class, args)`.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
