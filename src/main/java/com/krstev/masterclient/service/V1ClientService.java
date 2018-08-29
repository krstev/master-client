package com.krstev.masterclient.service;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Created by fkrstev | fkrstev@deployinc.com | 8/13/18.
 */
@Service
@Primary
public class V1ClientService implements ClientService {

  @Autowired
  DiscoveryClient client;

  @Autowired
  Executor executor;

  @Override
  public String getServiceName() {
    return "SERVICE";
  }

  @Override
  public Integer getServices() {
    List<String> instanceUrls = getInstanceUrls("SERVICE");

    List<CompletableFuture<Integer>> list = new LinkedList<>();
    instanceUrls.forEach(url -> {
      list.add(getInt(url));
    });

    int sum = list.stream().mapToInt(i -> {
      try {
        int r = i.get();
        logger.info("Result : " + r);
        return r;
      }
      catch (InterruptedException | ExecutionException e) {
        logger.error(e.getLocalizedMessage());
        throw new RuntimeException();
      }
    }).sum();

    logger.info("Sum : " + sum);

    return sum;

  }

  @Override
  public String getRandomHost() {
    List<ServiceInstance> instaces = client.getInstances("SERVICE");
    ServiceInstance randomInstance = instaces.get(new Random().nextInt(instaces.size()));
    return "http://" + randomInstance.getHost() + ":" + randomInstance.getPort();
  }

  @Override
  public List<String> getInstanceUrls(String serviceName) {
    return client.getInstances(serviceName).stream().
        map(instance -> "http://" + instance.getHost() + ":" + instance.getPort())
        .collect(Collectors.toList());
  }

  @Override
  public Executor getExecutor() {
    return executor;
  }
}
