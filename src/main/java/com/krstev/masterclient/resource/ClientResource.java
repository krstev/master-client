package com.krstev.masterclient.resource;

import com.krstev.masterclient.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * @author fkrstev
 * Created on 14-Feb-18
 */
@RestController
@EnableFeignClients
public class ClientResource {

    private static final Logger logger = LoggerFactory.getLogger(ClientResource.class);

    @Autowired
    DiscoveryClient client;

    @Autowired
    private ClientService clientService;

    @RequestMapping("/services")
    public Integer serviceInfo() throws Exception {
        List<ServiceInstance> instances = client.getInstances("SERVICE");

        List<CompletableFuture<Integer>> list = new LinkedList<>();
        instances.forEach(instance -> {
            try {
                list.add(clientService.getInt("http://" + instance.getHost() + ":" + instance.getPort()));
            } catch (InterruptedException e) {
                logger.error(e.getLocalizedMessage());
            }
        });

        return list.stream().mapToInt(i -> {
            try {
                return i.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getLocalizedMessage());
                throw new RuntimeException();
            }
        }).sum();
    }
}
