In Spring, the `@Async` annotation uses an `Executor` to manage the execution of asynchronous tasks. By default, Spring uses a `SimpleAsyncTaskExecutor`, which creates a new thread for each task. However, this is usually not suitable for production environments due to potential performance issues and resource exhaustion.

### Thread Management with `@Async`

When you use the `@Async` annotation, the behavior depends on the `Executor` configuration:

1. **Default Executor (SimpleAsyncTaskExecutor)**:
   - **New Thread for Each Task**: By default, `SimpleAsyncTaskExecutor` creates a new thread for each task. This is not recommended for production as it can lead to thread exhaustion and poor performance under heavy load.

2. **Custom Executor**:
   - **Thread Pool Executor**: To control the number of threads and manage resources more efficiently, you should configure a `ThreadPoolTaskExecutor`.

### Configuring a Custom Executor

You can define a custom `Executor` bean and configure it according to your needs. This allows you to reuse threads and manage the thread pool size.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // Minimum number of threads
        executor.setMaxPoolSize(20);   // Maximum number of threads
        executor.setQueueCapacity(100);  // Capacity of the queue
        executor.setThreadNamePrefix("Async-");  // Thread name prefix
        executor.initialize();
        return executor;
    }
}
```

### Using the Custom Executor

To use the custom `Executor`, annotate your Spring Boot application class with `@EnableAsync` and specify the executor to be used.

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Annotate your asynchronous methods with `@Async` and specify the custom executor bean name if necessary.

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    @Async("taskExecutor")
    public void asyncMethod() {
        // Your asynchronous code here
    }
}
```

### Summary

- **Default Behavior**: Using `@Async` with the default `SimpleAsyncTaskExecutor` creates a new thread for each task, which is not efficient for production use.
- **Custom Executor**: Configure a `ThreadPoolTaskExecutor` to manage a pool of threads, reuse threads, and control the maximum number of concurrent threads.
- **Configuration**: Customize the thread pool size, queue capacity, and thread naming to suit your application's needs.
  
By properly configuring the `Executor`, you can ensure efficient and scalable asynchronous processing in your Spring application.