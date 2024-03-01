package com.mshembelev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

class CrptApi{
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private Semaphore requestSemaphore;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.requestSemaphore = new Semaphore(requestLimit);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::resetSemaphore, 0, timeUnit.toSeconds(1), TimeUnit.SECONDS);
    }
    private void resetSemaphore(){
        requestSemaphore.drainPermits();
    }

    public void createDocument(){

    }


}