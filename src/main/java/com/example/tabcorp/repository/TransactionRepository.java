package com.example.tabcorp.repository;

import com.example.tabcorp.entity.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {
    @Query("select sum(t.total_Cost) from Transaction t where t.customer_Id = :customerId")
    Mono<Integer> getTotalCostPerCustomer(@Param("customerId") Long customerId);

    @Query("select sum(t.total_Cost) from Transaction t where t.product_Code = :productCode")
    Mono<Integer> getTotalCostPerProduct(@Param("productCode") String productCode);

    @Query("SELECT COUNT(t.id) " +
            "FROM transaction t " +
            "JOIN customer c ON t.customer_id = c.id " +
            "WHERE c.location = 'Australia'")
    Mono<Long> getCountByCustomerLocationAustralia();
}
