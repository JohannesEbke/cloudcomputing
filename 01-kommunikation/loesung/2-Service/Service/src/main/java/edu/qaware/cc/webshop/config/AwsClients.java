package edu.qaware.cc.webshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsClients {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }
}
