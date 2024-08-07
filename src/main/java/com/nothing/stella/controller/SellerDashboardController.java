package com.nothing.stella.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nothing.stella.exception.InvalidJWTHeaderException;
import com.nothing.stella.exception.OrderException;
import com.nothing.stella.exception.ProductException;
import com.nothing.stella.exception.UnknownErrorException;
import com.nothing.stella.exception.UserException;
import com.nothing.stella.model.ProductViewModel;
import com.nothing.stella.model.SellerOrderViewModel;
import com.nothing.stella.model.UpdateOrderItemStatusModal;
import com.nothing.stella.services.JWTService;
import com.nothing.stella.services.ProductService;
import com.nothing.stella.services.SellerDashboardService;

@RestController
@RequestMapping("/api/sellers/dashboard")
public class SellerDashboardController {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private SellerDashboardService sellerDashboardService;
    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(SellerDashboardController.class);

    @GetMapping("/orders")
    public List<SellerOrderViewModel> getSellerOrderViewModels(@RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {

            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return sellerDashboardService.fetchOrders(reference);
            } catch (UserException e) {
                throw e;
            } catch (ProductException e) {
                throw e;
            } catch (OrderException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @PostMapping("/update-order-status")
    public List<SellerOrderViewModel> updateOrderItemStatus(@RequestBody UpdateOrderItemStatusModal request,
            @RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {

            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return sellerDashboardService.updateOrderItemStatus(reference, request);
            } catch (UserException e) {
                throw e;
            } catch (ProductException e) {
                throw e;
            } catch (OrderException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

    @GetMapping("/products")
    public List<ProductViewModel> getProducts(@RequestHeader("Authorization") String jwtHeader) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {
            // extract token from request header
            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return productService.getProductsByReference(reference);

            } catch (ProductException e) {
                throw e;
            } catch (UserException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Unknown error: " + e.getMessage(), e);
                throw new UnknownErrorException("Error: unknown error");
            }
        } else {
            throw new InvalidJWTHeaderException("Error: Invalid JWTHeader");
        }
    }

}
