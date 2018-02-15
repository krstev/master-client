package com.krstev.masterclient.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * Created by fkrstev on 2/14/18.
 */
@Service
public class ClientService {

    private final RestTemplate restTemplate;

    public ClientService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate=restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<Integer> getInt(String host) throws InterruptedException {
        String url = host+"/nextInt";
        Integer results = restTemplate.getForObject(url, Integer.class);
        return CompletableFuture.completedFuture(results);
    }

}
class ServiceClient {
}
