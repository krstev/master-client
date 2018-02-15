package com.krstev.masterclient.resource;

import com.krstev.masterclient.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author fkrstev
 * Created on 14-Feb-18
 */
@RestController
@EnableFeignClients
public class ClientResource {

    @Autowired
    DiscoveryClient client;

    @Autowired
    private ClientService clientService;

    @RequestMapping("/services")
    public String serviceInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        List<CompletableFuture<Integer>> list = new LinkedList<>();
        client.getInstances("SERVICE").stream().forEach(serviceInstance -> {
            try {
                list.add(clientService.getInt(serviceInstance.getHost() + ":" + serviceInstance.getPort() + "\n"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture.allOf(list.toArray()).join();
        return stringBuilder.toString();
    }
}
