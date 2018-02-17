package com.krstev.masterclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
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
    public CompletableFuture<List<Integer>> getPrimes(String host, int lowerLimit, int upperLimit) throws InterruptedException {
        String url = host + "/primesNumbers?lower=" + lowerLimit + "&upper=" + upperLimit;
        List<Integer> primeNumbers = restTemplate.getForObject(url, List.class);
        return CompletableFuture.completedFuture(primeNumbers);
    }

    @Async
    public CompletableFuture<Integer> getVowels(String host, String text) throws InterruptedException {
        String url = host + "/countVowels?text=" + text;
        Integer results = restTemplate.getForObject(url, Integer.class);
        return CompletableFuture.completedFuture(results);
    }


}
