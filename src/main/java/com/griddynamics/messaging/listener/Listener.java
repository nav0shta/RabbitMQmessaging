package com.griddynamics.messaging.listener;

import com.griddynamics.messaging.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class Listener {

    private Long timestamp;

    public static void main(String[] args) {
        SpringApplication.run(Listener.class, args);
    }

    @RabbitListener(queues = "durable-queue")
    public void onMessage(Order order) {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }
        log.info("{} : {}", System.currentTimeMillis() - timestamp, order.toString());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setAddresses("127.0.0.1:30000,127.0.0.1:30002,127.0.0.1:30004");
        connectionFactory.setChannelCacheSize(10);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(20);
        return factory;
    }

    @Bean
    public Queue queue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "quorum");
        return new Queue("durable-queue", true, false, false, args);
    }
}
