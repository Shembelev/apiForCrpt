package com.mshembelev;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


//Класс для тестирования работы CrptApi
public class Test {
    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 100);
        CrptApi.Document document = createTestDocument();
        String signature = "Test";
        int numThreads = 10;
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                crptApi.createDocument(document, signature);
            });
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            System.out.println("Все запросы завершены");
        }).join();
    }

    private static CrptApi.Document createTestDocument() {
        List<CrptApi.Products> productsList = new ArrayList<>();
        productsList.add(createTestProduct());
        productsList.add(createTestProduct());
        productsList.add(createTestProduct());
        return new CrptApi.Document("12345",
                "Draft",
                "LP_INTRODUCE_GOODS",
                true,
                "1234567890",
                "0987654321",
                "1122334455",
                "2020-01-23",
                "Test Production",
                productsList,
                "2020-01-23",
                "ABC123"
        );
    }

    private static CrptApi.Products createTestProduct() {
        return new CrptApi.Products(
                "Test Certificate",
                "2020-01-23",
                "Certificate123",
                "1234567890",
                "1122334455",
                "2020-01-23",
                "TestTnved",
                "TestUitCode",
                "TestUituCode"
        );
    }
}
