package com.example.tabcorp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("transaction")
public class Transaction {
    @Id
    private Long id;

    private LocalDateTime transactionTime;
    private int quantity;
    private int totalCost;
    private Long customerId;
    private String productCode;

    public Transaction() {
    }

    public Transaction(Long id, LocalDateTime transactionTime, int quantity, int totalCost, Long customerId, String productCode) {
        this.id = id;
        this.transactionTime = transactionTime;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.customerId = customerId;
        this.productCode = productCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
