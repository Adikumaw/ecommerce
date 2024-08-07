package com.nothing.stella.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nothing.stella.exception.ImageException;
import com.nothing.stella.exception.InvalidJWTHeaderException;
import com.nothing.stella.exception.InvalidStoreNameException;
import com.nothing.stella.exception.ProductException;
import com.nothing.stella.exception.UnknownErrorException;
import com.nothing.stella.exception.UserException;
import com.nothing.stella.model.ProductInputModel;
import com.nothing.stella.model.ProductUpdateModel;
import com.nothing.stella.model.ProductViewModel;
import com.nothing.stella.services.JWTService;
import com.nothing.stella.services.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private JWTService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping
    public ProductViewModel create(@RequestHeader("Authorization") String jwtHeader,
            @RequestParam List<MultipartFile> images,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam int stock,
            @RequestParam String category,
            @RequestParam Boolean active) {

        if (jwtService.verifyJwtHeader(jwtHeader)) {

            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                ProductInputModel model = new ProductInputModel(name, description, price,
                        stock, category, active);

                if (images.get(0).isEmpty()) {
                    images = null;
                }

                return productService.save(reference, model, images);
            } catch (ImageException e) {
                throw e;
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

    @GetMapping("/store")
    public List<ProductViewModel> getProductsByStoreName(@RequestParam("store") String storeName) {
        // extract token from request header
        try {

            return productService.getProductsByStoreName(storeName);

        } catch (ProductException e) {
            throw e;
        } catch (UserException e) {
            throw e;
        } catch (InvalidStoreNameException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unknown error: " + e.getMessage(), e);
            throw new UnknownErrorException("Error: unknown error");
        }
    }

    @GetMapping("/search")
    public List<ProductViewModel> getProductsBySearch(@RequestParam("search") String name) {
        try {

            return productService.getProductsBySearch(name);

        } catch (ProductException e) {
            throw e;
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unknown error: " + e.getMessage(), e);
            throw new UnknownErrorException("Error: unknown error");
        }
    }

    @PutMapping
    public ProductViewModel update(@RequestHeader("Authorization") String jwtHeader,
            @RequestParam("images") List<MultipartFile> images,
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("category") String category,
            @RequestParam("active") boolean active) {

        if (jwtService.verifyJwtHeader(jwtHeader)) {

            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                ProductUpdateModel model = new ProductUpdateModel(id, name, description, price,
                        stock, category, active);

                if (images.get(0).isEmpty()) {
                    images = null;
                }

                return productService.update(reference, model, images);
            } catch (ImageException e) {
                throw e;
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

    @DeleteMapping("/de-activate")
    public ProductViewModel deactivate(@RequestHeader("Authorization") String jwtHeader,
            @RequestParam("id") int id) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {

            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return productService.deactivate(reference, id);
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

    @PostMapping("/activate")
    public ProductViewModel activate(@RequestHeader("Authorization") String jwtHeader,
            @RequestParam("id") int id) {
        if (jwtService.verifyJwtHeader(jwtHeader)) {

            String jwtToken = jwtHeader.substring(7);
            try {
                String reference = jwtService.fetchReference(jwtToken);

                return productService.activate(reference, id);
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
