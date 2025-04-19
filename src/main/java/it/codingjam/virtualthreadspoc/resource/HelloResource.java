package it.codingjam.virtualthreadspoc.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloResource {

    @GetMapping
    public String hello() throws InterruptedException {
        log.info("Before sleep on thread {}", Thread.currentThread());
        Thread.sleep(400);
        log.info("After sleep on thread {}", Thread.currentThread());
        log.info("Active count in group {}: {}", Thread.currentThread().getThreadGroup().getName(), Thread.currentThread().getThreadGroup().activeCount());

        return "Hello World";
    }

    @GetMapping("/parallel")
    public String parallelHello() throws ExecutionException, InterruptedException, TimeoutException {
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            Future<String> hello = executor.submit(() -> getMessage("Hello", 400));
            Future<String> world = executor.submit(() -> getMessage("World", 600));

            return hello.get(1, TimeUnit.SECONDS) + " " + world.get(1, TimeUnit.SECONDS);
        }
    }

    private static String getMessage(String message, long duration) {
        try {
            log.info("Before sleep on thread {} for {}", Thread.currentThread(), message);
            Thread.sleep(duration);
            log.info("After sleep on thread {} for {}", Thread.currentThread(), message);
            return message;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
