package com.krstev.masterclient.resource;

import com.krstev.masterclient.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * @author fkrstev
 *         Created on 14-Feb-18
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

        logger.info(instances.toString());

        List<CompletableFuture<Integer>> list = new LinkedList<>();
        instances.forEach(instance -> {
            try {
                list.add(clientService.getInt("http://" + instance.getHost() + ":" + instance.getPort()));
            } catch (InterruptedException e) {
                logger.error(e.getLocalizedMessage());
            }
        });

        int sum = list.stream().mapToInt(i -> {
            try {
                int r = i.get();
                logger.info("Result : " + r);
                return r;
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getLocalizedMessage());
                throw new RuntimeException();
            }
        }).sum();

        logger.info("Sum : " + sum);

        return sum;
    }

    @RequestMapping("/primeNumbers")
    public List<Integer> primeNumbers(@PathParam(value = "upperLimit") int upperLimit) throws Exception {
        List<ServiceInstance> instances = client.getInstances("SERVICE");

        int r = upperLimit / instances.size();

        List<CompletableFuture<List<Integer>>> list = new LinkedList<>();
        int start = 0;
        int end = r;

        for (ServiceInstance instance : instances) {
            list.add(clientService.getPrimes("http://" + instance.getHost() + ":" + instance.getPort(), start, end));
            start = end + 1;
            end = (start + r) < upperLimit ? start + r : upperLimit;
        }

        List<Integer> result = new LinkedList<>();
        list.forEach(res -> {
            try {
                result.addAll(res.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return result;
    }

    @RequestMapping("/countVowels")
    public Integer countVowels(@PathParam(value = "text") String text) throws Exception {
        List<ServiceInstance> instances = client.getInstances("SERVICE");

        int r = text.length() / instances.size();

        List<CompletableFuture<Integer>> list = new LinkedList<>();
        int start = 0;
        int end = r;

        for (ServiceInstance instance : instances) {
            list.add(clientService.getVowels("http://" + instance.getHost() + ":" + instance.getPort(), text.substring(start, end)));
            start = end + 1;
            end = (start + r) < text.length() ? start + r : text.length();
        }

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
