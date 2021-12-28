package ru.kalampus.RequestService.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.kalampus.RequestServcieApi.model.*;
import ru.kalampus.RequestService.exceptions.ExternalSystemException;
import ru.kalampus.RequestService.service.KafkaProducer;
import ru.kalampus.RequestServcieApi.api.RequestsApi;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
public class RequestServiceApiImpl implements RequestsApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestServiceApiImpl.class);

    @Value("${timeout_value}")
    private Integer timeoutValue;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public Mono<ResponseEntity<CreditResponse>> requestsPost(Mono<CreditRequest> creditRequest, ServerWebExchange exchange) {
        try {
            LOGGER.info("requestPost is working...");
            return creditRequest.flatMap(value -> {
                ListenableFuture<SendResult<String, Object>> listenableFuture = kafkaProducer.sendToKafka(value);
                try {
                    listenableFuture.get(timeoutValue, TimeUnit.SECONDS);
                } catch (Exception e) {
                    return Mono.just(createFailResponse(value.getRequestID(), "500", "Брокер Кафка не доступен", HttpStatus.SERVICE_UNAVAILABLE));
                }
                CreditResponse creditResponse = new CreditResponse();
                CreditResponseBody body = new CreditResponseBody();
                body.setStatusResponse(true);
                creditResponse.setBody(body);
                HeaderResponse headerResponse = new HeaderResponse();
                headerResponse.setMessageId(value.getRequestID());
                headerResponse.setMessageDate(new Date());
                creditResponse.setHeader(headerResponse);
                ResponseEntity<CreditResponse> responseEntity = new ResponseEntity<>(creditResponse, HttpStatus.OK);
                return Mono.just(responseEntity);
            });

        } catch (Throwable exception) {
            if (exception instanceof ExternalSystemException) {
                return creditRequest.flatMap(value ->
                        Mono.just(createFailResponse(value.getRequestID(), "500", exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE)));
            }
            else {
                return creditRequest.flatMap(value ->
                        Mono.just(createFailResponse(value.getRequestID(), "500", exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
            }
        }
    }

    private ResponseEntity<CreditResponse> createFailResponse(String messageId, String errorKod, String errorMessage, HttpStatus httpStatus) {
        CreditResponse creditResponse = new CreditResponse();
        CreditResponseBody body = new CreditResponseBody();
        body.setStatusResponse(false);
        StatusMessage statusMessage = new StatusMessage();
        statusMessage.setCode(errorKod);
        statusMessage.setText(errorMessage);
        HeaderResponse headerResponse = new HeaderResponse();
        headerResponse.setMessageId(messageId);
        headerResponse.setMessageDate(new Date());
        creditResponse.setStatusMessage(statusMessage);
        creditResponse.setBody(body);
        creditResponse.setHeader(headerResponse);
        return new ResponseEntity<>(creditResponse, httpStatus);
    }
}
