package ru.veretennikov;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.veretennikov.service.PrepareMusicService;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties()
public class SpringBootConsoleApplication implements CommandLineRunner{

    private final PrepareMusicService prepareMusicService;

    public static void main(String[]args) {
        logger.info("STARTING THE APPLICATION");
        SpringApplication.run(SpringBootConsoleApplication.class, args);
        logger.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        logger.info("EXECUTING : command line runner");
        prepareMusicService.execute();
    }

}
