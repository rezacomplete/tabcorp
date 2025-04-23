package com.example.tabcorp.controller;

import com.example.tabcorp.entity.Transaction;
import com.example.tabcorp.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll().block();
    }

    @Test
    void testCreateTransactionWithAdminRole() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionTime("2026-04-25T12:00:00");
        request.setCustomerId(10001L);
        request.setQuantity(2);
        request.setProductCode("PRODUCT_001");

        webTestClient.post()
                .uri("/transactions")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .value(id -> Assertions.assertNotNull(id));
    }

    @Test
    void testCreateTransactionWithAdminRoleWithTransactionInPast() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionTime("2020-04-25T12:00:00");
        request.setCustomerId(10001L);
        request.setQuantity(2);
        request.setProductCode("PRODUCT_001");

        webTestClient.post()
                .uri("/transactions")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateTransactionWithAdminRoleWithNonExistentCustomer() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionTime("2026-04-25T12:00:00");
        request.setCustomerId(10006L);
        request.setQuantity(2);
        request.setProductCode("PRODUCT_001");

        webTestClient.post()
                .uri("/transactions")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateTransactionWithAdminRoleWithNonExistentProduct() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionTime(LocalDateTime.now().plusMinutes(5).toString());
        request.setCustomerId(10001L);
        request.setQuantity(2);
        request.setProductCode("PRODUCT_0011");

        webTestClient.post()
                .uri("/transactions")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateTransactionWithNoAuthShouldReturn401() {
        TransactionRequest request = new TransactionRequest();
        request.setTransactionTime(LocalDateTime.now().plusMinutes(5).toString());
        request.setCustomerId(10001L);
        request.setQuantity(2);
        request.setProductCode("PRODUCT_001");

        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetTotalCostPerCustomerWithAdminRole() {
        transactionRepository.save(new Transaction(null, LocalDateTime.now().plusMinutes(5), 2, 100, 10001L, "PRODUCT_001")).block();

        webTestClient.get()
                .uri("/transactions/report/customer/10001")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(100);
    }

    @Test
    void testGetTotalCostPerCustomerWithoutAdminRole() {
        webTestClient.get()
                .uri("/transactions/report/customer/10001")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetTotalCostPerProductWithAdminRole() {
        transactionRepository.save(new Transaction(null, LocalDateTime.now().plusMinutes(5), 2, 100, 10001L, "PRODUCT_001")).block();

        webTestClient.get()
                .uri("/transactions/report/product/PRODUCT_001")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(100);
    }

    @Test
    void testGetTotalCostPerProductWithoutAdminRole() {
        webTestClient.get()
                .uri("/transactions/report/product/PRODUCT_001")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getCountAustralianCustomerTransactionsWithAdminRole() {
        transactionRepository.save(new Transaction(null, LocalDateTime.now().plusMinutes(5), 2, 100, 10001L, "PRODUCT_001")).block();

        webTestClient.get()
                .uri("/transactions/report/australian-customers")
                .headers(headers -> headers.setBasicAuth("admin", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(1);
    }

    @Test
    void getCountAustralianCustomerTransactionsWithoutAdminRole() {
        webTestClient.get()
                .uri("/transactions/report/australian-customers")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}