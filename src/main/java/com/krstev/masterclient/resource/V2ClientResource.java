package com.krstev.masterclient.resource;

import com.krstev.masterclient.models.CountVowelsRequest;
import com.krstev.masterclient.models.GoogleRequest;
import com.krstev.masterclient.models.PrimeNumberRequest;
import com.krstev.masterclient.service.ClientService;
import com.krstev.masterclient.service.V2ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by fkrstev | fkrstev@deployinc.com | 8/12/18.
 */

@RequestMapping("/v2")
@RestController
public class V2ClientResource {
  private static final Logger logger = LoggerFactory.getLogger(V2ClientResource.class);

  @Resource(name = "v2ClientService")
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
