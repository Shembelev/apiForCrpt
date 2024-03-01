package com.mshembelev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.*;

class CrptApi{
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private Semaphore requestSemaphore;
    private final String baseApi = "";

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

    static class Document {
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
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode documentNode = objectMapper.createObjectNode();

            ObjectNode descriptionNode = objectMapper.createObjectNode();
            descriptionNode.put("participantInn", participantInn);

            ArrayNode productsNode = objectMapper.createArrayNode();
            for (Products product : products) {
                productsNode.add(objectMapper.valueToTree(product));
            }

            documentNode.set("description", descriptionNode);
            documentNode.put("doc_id", docId);
            documentNode.put("doc_status", docStatus);
            documentNode.put("doc_type", docType);
            documentNode.put("importRequest", importRequest);
            documentNode.put("owner_inn", ownerInn);
            documentNode.put("participant_inn", participantInn);
            documentNode.put("producer_inn", producerInn);
            documentNode.put("production_date", productionDate);
            documentNode.put("production_type", productionType);
            documentNode.set("products", productsNode);
            documentNode.put("reg_date", regDate);
            documentNode.put("reg_number", regNumber);

            return documentNode.toString();
        }
    }

    static class Products {
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
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode productsNode = objectMapper.createObjectNode();

            productsNode.put("certificate_document", certificateDocument);
            productsNode.put("certificate_document_date", certificateDocumentDate);
            productsNode.put("certificate_document_number", certificateDocumentNumber);
            productsNode.put("owner_inn", ownerInn);
            productsNode.put("producer_inn", producerInn);
            productsNode.put("production_date", productionDate);
            productsNode.put("tnved_code", tnvedCode);
            productsNode.put("uit_code", uitCode);
            productsNode.put("uitu_code", uituCode);

            return productsNode.toString();
        }
    }

}