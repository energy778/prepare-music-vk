package ru.veretennikov.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties("application")
public class AppProperty {
    private String fileInput;
    private List<String> previouslyUploadedFiles;
    private String fileOutput;
    private String baseDir;
}
