package ru.kalampus.RequestService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.kalampus.RequestServcieApi.model.CreditRequest;
import ru.kalampus.RequestService.exceptions.ExternalSystemException;


@Service
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${topic-name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public ListenableFuture<SendResult<String, Object>> sendToKafka(CreditRequest creditRequest) {
        try {
            LOGGER.info("KafkaProducer is working");
            ListenableFuture<SendResult<String, Object>> kafkaResult = kafkaTemplate.send(topicName, "CreditRequest", creditRequest);
            LOGGER.info("Message is send");
            kafkaResult.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onFailure(Throwable ex) {
                    LOGGER.info("Get bad response from Kafka");
                    throw new ExternalSystemException(ex.getMessage());
                }

                @Override
                public void onSuccess(SendResult<String, Object> result) {

                    LOGGER.info("Get success response from Kafka");
                }
            });
            return kafkaResult;
        } catch (Throwable ex) {
            throw new ExternalSystemException(ex.getMessage());
        }
    }
}
