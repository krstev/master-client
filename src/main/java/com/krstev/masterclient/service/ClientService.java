package com.krstev.masterclient.service;

import com.krstev.masterclient.models.CountVowelsRequest;
import com.krstev.masterclient.models.GoogleRequest;
import com.krstev.masterclient.models.PrimeNumberRequest;
import com.krstev.masterclient.models.PrimeNumbers;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static java.util.concurrent.CompletableFuture.*;

/**
 * Created by fkrstev on 2/14/18.
 */

public interface ClientService {

  Logger logger = LoggerFactory.getLogger(ClientService.class);
  String EMPTY_STRING = "";
  String V2APIVERSION = "v2";
  Integer TIMEOUT_MILLISECONDS = 600;

  RestTemplate restTemplate = new RestTemplateBuilder().build();


  default Integer countVowels(CountVowelsRequest request) {
    ZonedDateTime startTime = logStart();
    List<String> instanceUrls = getInstanceUrls(getServiceName());
    String text = request.getText();
    int r = text.length() / instanceUrls.size();

    List<Integer> list = Collections.synchronizedList(new LinkedList<>());
    int start = 0;
    int end = r;

    List<CompletableFuture> completableFutures = new LinkedList<>();

    for (String url : instanceUrls) {
      int fStart = start;
      int fEnd = end;
      completableFutures.add(runAsync(() -> list.add(getVowels(url, text.substring(fStart, fEnd))), getExecutor()));
      start = end + 1;
      end = (start + r) < text.length() ? start + r : text.length();
    }


    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        completableFutures.toArray(new CompletableFuture[completableFutures.size()])
    );

    try {
      allOf(allFutures).get();
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    logEnd(startTime);

    return list.stream().mapToInt(Integer::intValue).sum();
  }

  default List<Integer> getPrimeNumbers(PrimeNumberRequest request) {
    ZonedDateTime startTime = logStart();
    List<String> instanceUrls = getInstanceUrls(getServiceName());

    int upperLimit = request.getLimit();
    int r = upperLimit / instanceUrls.size();

    List<PrimeNumbers> list = Collections.synchronizedList(new LinkedList<>());
    int start = 0;
    int end = r;
    List<CompletableFuture> completableFutures = new LinkedList<>();

    for (String url : instanceUrls) {
      int fStart = start;
      int fEnd = end;
      completableFutures.add(runAsync(() -> list.add(getPrimes(url, fStart, fEnd)), getExecutor()));
      start = end + 1;
      end = (start + r) < upperLimit ? start + r : upperLimit;
    }

    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        completableFutures.toArray(new CompletableFuture[completableFutures.size()])
    );

    try {
      allOf(allFutures).get();
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    List<Integer> result = new LinkedList<>();
    list.forEach(res ->
        result.addAll(res.getPrimeNumbers())
    );

    logEnd(startTime);

    return result;
  }

  String getServiceName();

  Integer getServices();

  @Async
  default CompletableFuture<Integer> getInt(String host) {
    String url = host + "/nextInt";
    logger.info("Url : " + url);
    Integer results = restTemplate.getForObject(url, Integer.class);
    logger.info("Result from service " + url + " is " + results);
    return CompletableFuture.completedFuture(results);
  }


  default PrimeNumbers getPrimes(String host, int lowerLimit, int upperLimit) {
    String url = host + "/primeNumbers?lower=" + lowerLimit + "&upper=" + upperLimit;
    logger.info("Url : " + url);
    ResponseEntity<PrimeNumbers> primeNumbers = restTemplate.getForEntity(url, PrimeNumbers.class);
    logger.info("Result from service " + url + " is " + primeNumbers.toString());
    return primeNumbers.getBody();
  }

  default Integer getVowels(String host, String text) {
    String url = host + "/countVowels?text=" + text;
    logger.info("Url : " + url);
    Integer results = restTemplate.getForObject(url, Integer.class);
    logger.info("Result from service " + url + " is " + results);
    return results;
  }


  default String searchGoogleQuery(GoogleRequest googleRequest) throws ExecutionException, InterruptedException {
    ZonedDateTime startTime = logStart();
    String search = googleRequest.getSearch();
    CompletableFuture<String>[] completableFutures = new CompletableFuture[]{
        supplyAsync(() -> searchWeb(search)),
        supplyAsync(() -> searchImage(search)),
        supplyAsync(() -> searchVideo(search))
    };

    List<String> results = new LinkedList<>();

    allOf(completableFutures)
        .thenAccept((f) -> Arrays.stream(completableFutures)
            .map(this::safeGet)
            .forEach(results::add)).get();

    logEnd(startTime);
    return StringUtils.join(results, " ; ");
  }

  default String searchGoogleQueryTimeout(GoogleRequest googleRequest) throws ExecutionException, InterruptedException {
    ZonedDateTime startTime = logStart();
    String search = googleRequest.getSearch();

    List<String> results = Collections.synchronizedList(new ArrayList<>());

    CompletableFuture<Void> timeout = runAsync(() -> timeout(TIMEOUT_MILLISECONDS));

    anyOf(
        allOf(
            runAsync(() -> results.add(searchWeb(search, V2APIVERSION))),
            runAsync(() -> results.add(searchImage(search, V2APIVERSION))),
            runAsync(() -> results.add(searchVideo(search, V2APIVERSION)))
        ),
        timeout
    ).get();

    logEnd(startTime);
    return results.size() < 3 ? "Timeout" : String.join(" : ", results);
  }

  default String searchGoogleQueryTimeoutReplica(GoogleRequest googleRequest) throws ExecutionException, InterruptedException {
    ZonedDateTime startTime = logStart();
    String search = googleRequest.getSearch();

    List<String> results = Collections.synchronizedList(new ArrayList<>());

    CompletableFuture<Void> timeout = runAsync(() -> timeout(TIMEOUT_MILLISECONDS));

    anyOf(
        allOf(
            anyOf(runAsync(() -> results.add(searchWeb(search, V2APIVERSION))), runAsync(() -> results.add(searchWeb(search, V2APIVERSION)))),
            anyOf(runAsync(() -> results.add(searchImage(search, V2APIVERSION))), runAsync(() -> results.add(searchImage(search, V2APIVERSION)))),
            anyOf(runAsync(() -> results.add(searchVideo(search, V2APIVERSION))), runAsync(() -> results.add(searchVideo(search, V2APIVERSION))))
        ),
        timeout
    ).get();

    logEnd(startTime);
    return results.size() < 3 ? "Timeout" : String.join(" : ", results);
  }

  default String safeGet(CompletableFuture<String> future) {
    try {
      return future.get();
    }
    catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      logger.error(e.getLocalizedMessage());
    }

    return EMPTY_STRING;
  }

  default void timeout(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      logger.error(e.getLocalizedMessage());
    }
  }

  default String searchWeb(String search) {
    return search("web", search);
  }

  default String searchImage(String search) {
    return search("image", search);
  }

  default String searchVideo(String search) {
    return search("video", search);
  }

  default String searchWeb(String search, String apiVersion) {
    return search("web", search, apiVersion);
  }

  default String searchImage(String search, String apiVersion) {
    return search("image", search, apiVersion);
  }

  default String searchVideo(String search, String apiVersion) {
    return search("video", search, apiVersion);
  }

  default String search(String type, String search) {
    return search(type, search, "v1");
  }

  default String search(String type, String search, String apiVersion) {
    String url = getRandomHost() + "/google/" + apiVersion + "/" + type + "?search=" + search;
    logger.info("Url : " + url);
    String results = restTemplate.getForObject(url, String.class);
    logger.info("Result from service " + url + " is " + results);
    return results;
  }

  String getRandomHost();

  default ZonedDateTime logStart() {
    ZonedDateTime startTime = ZonedDateTime.now();
    logger.info("Start time : {}", startTime);
    return startTime;
  }

  default void logEnd(ZonedDateTime startTime) {
    ZonedDateTime endTime = ZonedDateTime.now();
    logger.info("End time : {}", endTime);
    logger.info("Total : {}", Duration.between(endTime, startTime));
  }

  List<String> getInstanceUrls(String serviceName);

  Executor getExecutor();

}
