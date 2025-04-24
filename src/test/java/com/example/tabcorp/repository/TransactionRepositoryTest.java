package com.example.tabcorp.repository;

import com.example.tabcorp.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll().block();

        Transaction t1 = new Transaction(null, LocalDateTime.now(), 2, 1000, 10001L, "PRODUCT_001");
        Transaction t2 = new Transaction(null, LocalDateTime.now(), 1, 500, 10001L, "PRODUCT_001");
        transactionRepository.saveAll(List.of(t1, t2)).collectList().block();
    }

    @Test
    void testGetTotalCostPerCustomer() {
        Integer total = transactionRepository.getTotalCostPerCustomer(10001L).block();
        assertEquals(1500, total);
    }

    @Test
    void testGetTotalCostPerProduct() {
        Integer total = transactionRepository.getTotalCostPerProduct("PRODUCT_001").block();
        assertEquals(1500, total);
    }

    @Test
    void testGetCountByCustomerLocationAustralia() {
        Long count = transactionRepository.getCountByCustomerLocationAustralia().block();
        assertEquals(2L, count); // 2 transactions by Australian customer
    }
}