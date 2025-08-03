package com.gameet.global.config.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.ses")
@Getter
@Setter
public class AwsProperties {

    private String accessKey;
    private String secretKey;
    private String region;
}
