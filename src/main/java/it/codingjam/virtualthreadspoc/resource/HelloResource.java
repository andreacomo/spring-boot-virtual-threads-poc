package it.codingjam.virtualthreadspoc.resource;

import it.codingjam.virtualthreadspoc.utils.Futures;
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
    public String parallelHello() {
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            Future<String> hello = executor.submit(() -> getMessage("Hello", 400));
            Future<String> world = executor.submit(() -> getMessage("World", 600));

            return Futures.join(hello, world, (h, w) -> h + " " + w);
        }
    }

    @GetMapping("/parallel/result")
    public String parallelHelloResult() throws Exception {
        // this approach is recommended when using thread pool to not leave loose threads on exceptions
        // it's not robust on CancellationException and InterruptException
        // (maybe parallelHello approach is more Java-like -- this is more Rust-like).
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            Future<Result<String, Exception>> hello = executor.submit(() -> getResult("Hello", 400));
            Future<Result<String, Exception>> world = executor.submit(() -> getResult("World", 600));

            return switch (hello.get()) {
                case Result.Ok(String h) -> switch (world.get()) {
                    case Result.Ok(String w) -> h + " " + w;
                    case Result.Err(Exception e) -> {
                        hello.cancel(true);
                        throw e;
                    }
                };
                case Result.Err(Exception e) -> {
                    world.cancel(true);
                    throw e;
                }
            };
        }
    }

    private static Result<String, Exception> getResult(String message, long duration) {
        try {
            log.info("Before sleep on thread {} for {}", Thread.currentThread(), message);
            Thread.sleep(duration);
            log.info("After sleep on thread {} for {}", Thread.currentThread(), message);
            return new Result.Ok<>(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result.Err<>(e);
        }
    }

    private static String getMessage(String message, long duration) {
        try {
            log.info("Before sleep on thread {} for {}", Thread.currentThread(), message);
            Thread.sleep(duration);
            log.info("After sleep on thread {} for {}", Thread.currentThread(), message);
            return message;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    sealed interface Result<T, E> permits Result.Ok, Result.Err {
        record Ok<T, E extends Exception>(T value) implements Result<T, E> {}
        record Err<T, E extends Exception>(E e) implements Result<T, E> {}
    }
}
