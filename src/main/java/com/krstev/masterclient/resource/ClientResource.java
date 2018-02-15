package com.krstev.masterclient.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author fkrstev
 * Created on 14-Feb-18
 */
@RestController
@EnableFeignClients
public class ClientResource {

    @Autowired
    DiscoveryClient client;

    @RequestMapping("/services")
    public String serviceInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        client.getInstances("SERVICE").stream().forEach(serviceInstance -> stringBuilder.append(serviceInstance.getHost() + ":" + serviceInstance.getPort() + "\n"));
        return stringBuilder.toString();
    }
}
