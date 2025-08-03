package semigg.semi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "riot.api")
@Component
public class RiotApiProperties {
    private String key;
    private String riotregionUrl;
    private String matchUrl;
    private String platformUrl;
}