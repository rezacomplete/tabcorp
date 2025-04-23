package com.example.tabcorp.controller;

import com.example.tabcorp.exception.InvalidDataException;
import com.example.tabcorp.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Long>> createUsingJson(@RequestBody Mono<TransactionRequest> requestMono, Mono<Principal> principal) {
        return requestMono
                .flatMap(service::saveTransaction)
                .map(ResponseEntity::ok);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<Long>> createUsingBinary(@RequestBody Mono<byte[]> binaryRequestMono) {
        return binaryRequestMono
                // Make json string from the byte array
                .map(String::new)
                .flatMap(json -> {
                    try {
                        TransactionRequest request = objectMapper.readValue(json, TransactionRequest.class);
                        return service.saveTransaction(request);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new InvalidDataException("Invalid binary format: " + e.getMessage()));
                    }
                })
                .map(ResponseEntity::ok);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report/customer/{id}")
    public Mono<ResponseEntity<Integer>> getTotalCostPerCustomer(@PathVariable Long id) {
        return service.getTotalCostPerCustomer(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report/product/{code}")
    public Mono<ResponseEntity<Integer>> getTotalCostPerProduct(@PathVariable String code) {
        return service.getTotalCostPerProduct(code)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/report/australian-customers")
    public Mono<ResponseEntity<Long>> getCountAustralianCustomerTransactions() {
        return service.getCountByCustomerLocationAustralia()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
