package com.maleki.narges.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")//this config is not available in the production phase
public class AutoCreateConfig {
    @Bean
    public NewTopic libraryEvents(){
        return TopicBuilder
                .name("libraryEvents")
                .partitions(3)
                //the number of kafka brokers
                //.replicas(4)
                .build();
    }
}
