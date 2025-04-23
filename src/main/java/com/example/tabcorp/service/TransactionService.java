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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    private static final String ACTIVE = "Active";
    private static final int MAX_TOTAL_ACCEPTED_COST = 5000;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

//    public Long saveTransaction(TransactionRequest request) {
//        LocalDateTime time = LocalDateTime.parse(request.getTransactionTime());
//        if (time.isBefore(LocalDateTime.now())) {
//            throw new InvalidDataException("Date must not be in the past");
//        }
//
//        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
//                () -> new ResourceNotFoundException("Customer not found")
//        );
//
//        Product product = productRepository.findById(request.getProductCode()).orElseThrow(
//                () -> new ResourceNotFoundException("Product not found")
//        );
//
//        if (!ACTIVE.equalsIgnoreCase(product.getStatus())) {
//            throw new RuntimeException("Product must be active");
//        }
//
//        int total = product.getCost() * request.getQuantity();
//        if (total > MAX_TOTAL_ACCEPTED_COST) {
//            throw new InvalidDataException("Total cost must not exceed " + MAX_TOTAL_ACCEPTED_COST);
//        }
//
//        Transaction transaction = new Transaction();
//        transaction.setTransactionTime(time);
//        transaction.setCustomer(customer);
//        transaction.setProduct(product);
//        transaction.setQuantity(request.getQuantity());
//        transaction.setTotalCost(total);
//
//        return transactionRepository.save(transaction).getId();
//    }

    public Mono<Integer> getTotalCostPerCustomer(Long customerId) {
        return transactionRepository.getTotalCostPerCustomer(customerId);
    }

    public Mono<Integer> getTotalCostPerProduct(String productCode) {
        return transactionRepository.getTotalCostPerProduct(productCode);
    }

    public Mono<Long> getCountByCustomerLocationAustralia() {
        return transactionRepository.getCountByCustomerLocationAustralia();
    }

    public Mono<Long> saveTransaction(TransactionRequest request) {
        LocalDateTime time = LocalDateTime.parse(request.getTransactionTime());
        if (time.isBefore(LocalDateTime.now())) {
            return Mono.error(new InvalidDataException("Date must not be in the past"));
        }

        return customerRepository.findById(request.getCustomerId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Customer not found")))
                .zipWith(productRepository.findById(request.getProductCode())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product not found"))))
                .flatMap(tuple -> {
                    Customer customer = tuple.getT1();
                    Product product = tuple.getT2();

                    if (!ACTIVE.equalsIgnoreCase(product.getStatus())) {
                        return Mono.error(new RuntimeException("Product must be active"));
                    }

                    int total = product.getCost() * request.getQuantity();
                    if (total > MAX_TOTAL_ACCEPTED_COST) {
                        return Mono.error(new InvalidDataException("Total cost must not exceed " + MAX_TOTAL_ACCEPTED_COST));
                    }

                    Transaction transaction = new Transaction();
                    transaction.setTransactionTime(time);
                    transaction.setCustomerId(customer.getId());
                    transaction.setProductCode(product.getCode());
                    transaction.setQuantity(request.getQuantity());
                    transaction.setTotalCost(total);

                    return transactionRepository.save(transaction).map(Transaction::getId);
                });
    }

}
