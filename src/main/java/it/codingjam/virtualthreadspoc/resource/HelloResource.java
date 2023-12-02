package it.codingjam.virtualthreadspoc.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
