package ru.veretennikov;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.veretennikov.service.PrepareMusicService;

@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties()
public class SpringBootConsoleApplication implements CommandLineRunner{

    private static Logger LOG = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

    private final PrepareMusicService prepareMusicService;

    public static void main(String[]args) {
        LOG.info("STARTING THE APPLICATION");
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringBootConsoleApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }


    @Override
    public void run(String... args) {

        LOG.info("EXECUTING : command line runner");
        prepareMusicService.execute();

    }

}