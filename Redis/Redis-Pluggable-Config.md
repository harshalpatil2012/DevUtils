# Spring Boot with Redisson and Spring Session

This repository demonstrates how to use Redisson with Spring Boot and Spring Session for session management. You can easily configure Redisson to store session data in Redis when Redis is enabled.

## Getting Started

### Prerequisites

Before you begin, ensure you have the following prerequisites:

- Java Development Kit (JDK) 8 or higher
- Spring Boot
- Redis server (local or remote)

### Dependencies

In `build.gradle` or `pom.xml`, add the necessary dependencies:

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // Add Redisson and Spring Session dependencies
    if (project.hasProperty('myapp.redis.enabled') && project.property('myapp.redis.enabled').toBoolean()) {
        implementation 'org.redisson:redisson-spring-boot-starter:3.16.3'
    }
}


### Configuration
Create a Redisson configuration class to configure RedissonClient:
java

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
@Profile("redis")
public class RedissonSessionConfig {

    @Value("${myapp.redis.enabled}")
    private boolean redisEnabled;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        if (redisEnabled) {
            Config config = new Config();
            config.useSingleServer()
                  .setAddress("redis://localhost:6379"); // Configure Redis server address here

            return Redisson.create(config);
        } else {
            return null; // Return null when Redis is disabled
        }
    }
}
Set the active profile in application.properties or application.yml:
properties

# Enable the "redis" profile when Redis is enabled
spring.profiles.active=redis
Usage
Now, you can use Redisson-backed Spring Session for session management. You can inject HttpSession in controllers as shown in the previous example.

Run the Application
Make sure Redis server is running and accessible.

To run the Spring Boot application:


./gradlew bootRun


This consolidated `README.md` includes all the relevant information in a clear and organized manner. Make sure to customize it with specific details, versions, and server configurations as needed.


