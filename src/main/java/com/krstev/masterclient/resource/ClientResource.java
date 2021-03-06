package com.krstev.masterclient.resource;

import com.krstev.masterclient.models.CountVowelsRequest;
import com.krstev.masterclient.models.GoogleRequest;
import com.krstev.masterclient.models.PrimeNumberRequest;
import com.krstev.masterclient.service.ClientService;
import com.krstev.masterclient.service.V1ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
  public Integer serviceInfo() {
    return clientService.getServices();
  }

  @RequestMapping("/primeNumbers")
  public List<Integer> primeNumbers(@RequestBody PrimeNumberRequest request) {
    return clientService.getPrimeNumbers(request);
  }

  @RequestMapping("/countVowels")
  public Integer countVowels(@RequestBody CountVowelsRequest request) {
    return clientService.countVowels(request);
  }

  @RequestMapping("/googleQuery")
  public String google(@RequestBody GoogleRequest googleRequest) throws ExecutionException, InterruptedException {
    return clientService.searchGoogleQuery(googleRequest);
  }

  @RequestMapping("/googleQueryTimeout")
  public String googleTimeout(@RequestBody GoogleRequest googleRequest) throws ExecutionException, InterruptedException {
    return clientService.searchGoogleQueryTimeout(googleRequest);
  }

  @RequestMapping("/googleQueryTimeoutReplica")
  public String googleTimeoutReplica(@RequestBody GoogleRequest googleRequest) throws ExecutionException, InterruptedException {
    return clientService.searchGoogleQueryTimeoutReplica(googleRequest);
  }
}
