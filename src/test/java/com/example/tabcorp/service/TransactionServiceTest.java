package com.example.tabcorp.service;

import com.example.tabcorp.controller.TransactionRequest;
import com.example.tabcorp.entity.Customer;
import com.example.tabcorp.entity.Product;
import com.example.tabcorp.entity.Transaction;
import com.example.tabcorp.exception.InvalidDataException;
import com.example.tabcorp.exception.ResourceNotFoundException;
import com.example.tabcorp.repository.CustomerRepository;
import com.example.tabcorp.repository.ProductRepository;
import com.example.tabcorp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final long CUSTOMER_ID = 10001L;
    private static final String PRODUCT_CODE = "PRODUCT_001";
    private static final int QUANTITY = 2;
    private static final int COST = 500;
    private static final int MAX_TOTAL = 5000;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService service;

    @BeforeEach
    void setup() {
        service = new TransactionService(transactionRepository, customerRepository, productRepository);
    }

    private TransactionRequest createValidRequest() {
        return new TransactionRequest(
                LocalDateTime.now().plusMinutes(5).toString(), // In the future
                CUSTOMER_ID,
                QUANTITY,
                PRODUCT_CODE
        );
    }

    @Test
    void shouldThrowIfTransactionTimeInPast() {
        TransactionRequest request = new TransactionRequest(
                LocalDateTime.now().minusDays(1).toString(), // In the past
                CUSTOMER_ID,
                QUANTITY,
                PRODUCT_CODE
        );

        StepVerifier.create(service.saveTransaction(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidDataException && throwable.getMessage().equals("Date must not be in the past"))
                .verify();
    }

    @Test
    void shouldThrowIfCustomerNotFound() {
        TransactionRequest request = createValidRequest();

        Mockito.when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.empty());
        Mockito.when(productRepository.findById(PRODUCT_CODE)).thenReturn(Mono.empty());

        StepVerifier.create(service.saveTransaction(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException && throwable.getMessage().equals("Customer not found"))
                .verify();
    }

    @Test
    void shouldThrowIfProductNotFound() {
        TransactionRequest request = createValidRequest();

        Customer customer = new Customer(CUSTOMER_ID, "Reza", "Ahmadi", "email", "Australia");

        Mockito.when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customer));
        Mockito.when(productRepository.findById(PRODUCT_CODE)).thenReturn(Mono.empty());

        StepVerifier.create(service.saveTransaction(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException && throwable.getMessage().equals("Product not found"))
                .verify();
    }

    @Test
    void shouldThrowIfProductIsInactive() {
        TransactionRequest request = createValidRequest();

        Customer customer = new Customer(CUSTOMER_ID, "Reza", "Ahmadi", "email", "Australia");
        Product product = new Product(PRODUCT_CODE, COST, "Inactive");

        Mockito.when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customer));
        Mockito.when(productRepository.findById(PRODUCT_CODE)).thenReturn(Mono.just(product));

        StepVerifier.create(service.saveTransaction(request))
                .expectErrorMatches(t -> t instanceof RuntimeException && t.getMessage().equals("Product must be active"))
                .verify();
    }

    @Test
    void shouldThrowIfTotalCostTooHigh() {
        TransactionRequest request = createValidRequest();
        request.setQuantity(1000); // Exceeds MAX_TOTAL_ACCEPTED_COST

        Customer customer = new Customer(CUSTOMER_ID, "Reza", "Ahmadi", "email", "AU");
        Product product = new Product(PRODUCT_CODE, COST, "Active");

        Mockito.when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customer));
        Mockito.when(productRepository.findById(PRODUCT_CODE)).thenReturn(Mono.just(product));

        StepVerifier.create(service.saveTransaction(request))
                .expectErrorMatches(t -> t instanceof InvalidDataException && t.getMessage().contains("Total cost must not exceed"))
                .verify();
    }

    @Test
    void shouldSaveTransactionSuccessfully() {
        TransactionRequest request = createValidRequest();

        Customer customer = new Customer(CUSTOMER_ID, "Reza", "Ahmadi", "email", "AU");
        Product product = new Product(PRODUCT_CODE, COST, "Active");

        Mockito.when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Mono.just(customer));
        Mockito.when(productRepository.findById(PRODUCT_CODE)).thenReturn(Mono.just(product));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction tx = invocation.getArgument(0);
                    tx.setId(10001L);
                    return Mono.just(tx);
                });

        StepVerifier.create(service.saveTransaction(request))
                .expectNext(10001L)
                .verifyComplete();
    }

}