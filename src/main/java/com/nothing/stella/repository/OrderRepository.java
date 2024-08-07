package com.nothing.stella.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nothing.stella.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByRazorpayId(String razorpayId);

    @Query("SELECT o.status FROM Order o WHERE o.orderId = ?1")
    Optional<String> findStatusByOrderId(int orderId);

    List<Order> findByUserId(int userId);

    @Query("SELECT o.orderDate FROM Order o WHERE o.orderId = ?1 AND o.status != 'created' AND o.status != 'creating'")
    Optional<Date> findPaidOrderDateByOrderId(int orderId);

    @Query("SELECT o.orderId FROM Order o WHERE o.userId = ?1")
    List<Integer> findOrderIdByUserId(int userId);

}
