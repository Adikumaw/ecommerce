package com.nothing.stella.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nothing.stella.entity.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    @Query("SELECT pc.id FROM ProductCategory pc WHERE pc.category = ?1")
    Optional<Integer> findIdByCategory(String category);
}
