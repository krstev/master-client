package com.krstev.masterclient.service;

import com.krstev.masterclient.models.PrimeNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Created by fkrstev on 2/14/18.
 */
@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final RestTemplate restTemplate;

    public ClientService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<Integer> getInt(String host) throws InterruptedException {
        String url = host + "/nextInt";
        logger.info("Url : " + url);
        Integer results = restTemplate.getForObject(url, Integer.class);
        logger.info("Result from service " + url + " is " + results);
        return CompletableFuture.completedFuture(results);
    }

    @Async
    public CompletableFuture<PrimeNumbers> getPrimes(String host, int lowerLimit, int upperLimit) throws InterruptedException {
        String url = host + "/primeNumbers?lower=" + lowerLimit + "&upper=" + upperLimit;
        logger.info("Url : " + url);
        ResponseEntity<PrimeNumbers> primeNumbers = restTemplate.getForEntity(url, PrimeNumbers.class);
        logger.info("Result from service " + url + " is " + primeNumbers.toString());
        return CompletableFuture.completedFuture(primeNumbers.getBody());
    }

    @Async
    public CompletableFuture<Integer> getVowels(String host, String text) throws InterruptedException {
        String url = host + "/countVowels?text=" + text;
        logger.info("Url : " + url);
        Integer results = restTemplate.getForObject(url, Integer.class);
        logger.info("Result from service " + url + " is " + results);
        return CompletableFuture.completedFuture(results);
    }


}
