package com.griddynamics.messaging.sender;

import com.google.common.util.concurrent.RateLimiter;
import com.griddynamics.messaging.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.Random;

@SpringBootApplication
@Slf4j
public class Sender {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private RateLimiter rateLimiter;

    public static void main(String[] args) {
        SpringApplication.run(Sender.class, args);
    }

    @PostConstruct
    public void send() {
        while(true) {
            rateLimiter.acquire();
            int id = new Random().nextInt(100000);
            template.convertAndSend(new Order(id, "TEST"+id));
        }
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setAddresses("127.0.0.1:30000,127.0.0.1:30002,,127.0.0.1:30004");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate template() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange("ex.direct");
        return rabbitTemplate;
    }

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(10);
    }
}
