package com.aica.aivoca.wordinfo.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private String apiKey;
    private String url;

//    @PostConstruct
//    public void debug() {
//        System.out.println("🔍 KEY: " + apiKey);
//        System.out.println("🔍 URL: " + url);
//    }
}
