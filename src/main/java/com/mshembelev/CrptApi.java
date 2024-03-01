package com.mshembelev;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class CrptApi{
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger requestsLeft;
    private final int requestLimit;
    private final long timeInterval;

    private final HttpClient httpClient;
    private Semaphore requestSemaphore;
    private final String baseApi = "https://ismp.crpt.ru/api/v3/lk/";

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.httpClient = HttpClient.newHttpClient();
        this.requestSemaphore = new Semaphore(requestLimit);

        this.requestLimit = requestLimit;
        this.requestsLeft = new AtomicInteger(requestLimit);
        this.timeInterval = timeUnit.toMillis(1) / requestLimit;
        this.scheduler = Executors.newScheduledThreadPool(1);
        long initialDelay = 0;
        scheduler.scheduleAtFixedRate(this::resetRequests, initialDelay, timeInterval, TimeUnit.MILLISECONDS);
    }

    private void resetRequests() {
        requestsLeft.set(requestLimit);
    }

    public void createDocument(Document document, String signature) {
        try {
            if (requestsLeft.getAndDecrement() > 0) {
                String requestBody = document.toString();
                requestBody = requestBody.substring(0, requestBody.length() - 1) + ",\"signature\":\"" + signature + "\"}";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseApi + "/documents/create"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                //HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                //System.out.println(response);
            } else {
                System.out.println("Лимит исчерпан, запрос не может быть отправлен");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.getMessage());
        } finally {
            requestSemaphore.release();
        }
    }

    public static class Document {
        private String docId;
        private String docStatus;
        private String docType;
        private boolean importRequest;
        private String ownerInn;
        private String participantInn;
        private String producerInn;
        private String productionDate;
        private String productionType;
        private List<Products> products;
        private String regDate;
        private String regNumber;

        public Document(String docId, String docStatus, String docType, boolean importRequest, String ownerInn, String participantInn, String producerInn, String productionDate, String productionType, List<Products> products, String regDate, String regNumber) {
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.products = products;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }

        @Override
        public String toString() {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"description\": {\"participantInn\": \"").append(participantInn).append("\"},");
            json.append("\"doc_id\": \"").append(docId).append("\",");
            json.append("\"doc_status\": \"").append(docStatus).append("\",");
            json.append("\"doc_type\": \"").append(docType).append("\",");
            json.append("\"importRequest\": ").append(importRequest).append(",");
            json.append("\"owner_inn\": \"").append(ownerInn).append("\",");
            json.append("\"participant_inn\": \"").append(participantInn).append("\",");
            json.append("\"producer_inn\": \"").append(producerInn).append("\",");
            json.append("\"production_date\": \"").append(productionDate).append("\",");
            json.append("\"production_type\": \"").append(productionType).append("\",");
            json.append("\"products\": [");

            for (int i = 0; i < products.size(); i++) {
                json.append(products.get(i).toString());
                if (i < products.size() - 1) {
                    json.append(",");
                }
            }

            json.append("],");
            json.append("\"reg_date\": \"").append(regDate).append("\",");
            json.append("\"reg_number\": \"").append(regNumber).append("\"}");
            return json.toString();
        }

    }

    public static class Products {
        private String certificateDocument;
        private String certificateDocumentDate;
        private String certificateDocumentNumber;
        private String ownerInn;
        private String producerInn;
        private String productionDate;
        private String tnvedCode;
        private String uitCode;
        private String uituCode;

        public Products(String certificateDocument, String certificateDocumentDate, String certificateDocumentNumber, String ownerInn, String producerInn, String productionDate, String tnvedCode, String uitCode, String uituCode) {
            this.certificateDocument = certificateDocument;
            this.certificateDocumentDate = certificateDocumentDate;
            this.certificateDocumentNumber = certificateDocumentNumber;
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
            this.uitCode = uitCode;
            this.uituCode = uituCode;
        }

        @Override
        public String toString() {
            return "{\"certificate_document\": \"" + certificateDocument + "\","
                    + "\"certificate_document_date\": \"" + certificateDocumentDate + "\","
                    + "\"certificate_document_number\": \"" + certificateDocumentNumber + "\","
                    + "\"owner_inn\": \"" + ownerInn + "\","
                    + "\"producer_inn\": \"" + producerInn + "\","
                    + "\"production_date\": \"" + productionDate + "\","
                    + "\"tnved_code\": \"" + tnvedCode + "\","
                    + "\"uit_code\": \"" + uitCode + "\","
                    + "\"uitu_code\": \"" + uituCode + "\"}";
        }
    }
}