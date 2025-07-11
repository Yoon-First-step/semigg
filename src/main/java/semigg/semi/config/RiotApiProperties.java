package semigg.semi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "riot.api")
public class RiotApiProperties {
    private String key;
    private String regionUrl;
    private String platformUrl;
    // getter, setter
}
