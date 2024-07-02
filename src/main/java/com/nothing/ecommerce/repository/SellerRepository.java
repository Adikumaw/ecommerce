package com.nothing.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nothing.ecommerce.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer> {

    @Query("SELECT s.userId FROM Seller s WHERE s.storeName = ?1")
    int findUserIdByStoreName(String storeName);

    Boolean existsByStoreName(String storeName);

    Boolean existsByAddress(String address);
}
