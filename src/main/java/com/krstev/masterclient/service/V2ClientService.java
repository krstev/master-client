package com.krstev.masterclient.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by fkrstev | fkrstev@deployinc.com | 8/13/18.
 */

@Service
public class V2ClientService implements ClientService {

  @Value("${minikube.servicePort}")
  private Integer minikubeServicePort;

  @Value("${minikube.serviceName}")
  private String minikubeServiceName;

  @Autowired
  Executor executor;

  RestTemplate restTemplate = new RestTemplateBuilder().build();


  @Override
  public String getServiceName() {
    return minikubeServiceName;
  }

  @Override
  public Integer getServices() {
    return null;
  }

  @Override
  public String getRandomHost() {
    return "http://192.168.99.100:" + minikubeServicePort;
  }

  @Override
  public List<String> getInstanceUrls(String serviceName) {
    String url = "http://127.0.0.1:8000/api/v1/namespaces/default/endpoints/" + getServiceName();
    String jsonResponse = restTemplate.getForObject(url, String.class);
    Integer numberOfInstance = 0;
    try {
      JSONObject json = new JSONObject(jsonResponse);
      numberOfInstance = json.getJSONArray("subsets").getJSONObject(0).getJSONArray("addresses").length();
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
    List<String> results = new LinkedList<>();
    for (int i = 0; i < numberOfInstance; i++) {
      results.add(getRandomHost());
    }
    return results;
  }

  @Override
  public Executor getExecutor() {
    return executor;
  }
}
