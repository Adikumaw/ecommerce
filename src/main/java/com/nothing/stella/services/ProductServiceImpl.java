package com.nothing.stella.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nothing.stella.entity.Product;
import com.nothing.stella.entity.ProductCategory;
import com.nothing.stella.exception.EmptyImagesException;
import com.nothing.stella.exception.ImageException;
import com.nothing.stella.exception.InvalidProductCategoryException;
import com.nothing.stella.exception.InvalidProductException;
import com.nothing.stella.exception.InvalidProductIdException;
import com.nothing.stella.exception.InvalidStoreNameException;
import com.nothing.stella.exception.UnAuthorizedUserException;
import com.nothing.stella.exception.UsedProductNameException;
import com.nothing.stella.model.ProductIdAndNameModel;
import com.nothing.stella.model.ProductInputModel;
import com.nothing.stella.model.ProductUpdateModel;
import com.nothing.stella.model.ProductViewModel;
import com.nothing.stella.repository.ProductCategoryRepository;
import com.nothing.stella.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductCategoryRepository categoryRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private SellerService sellerService;
    private final static String path = "/home/all_father/Documents/workshop/java/Stella/src/main/resources/static/products";

    @Override
    public ProductViewModel save(String reference, ProductInputModel model, List<MultipartFile> images) {
        // verify product input model
        if (!verify(model)) {
            throw new InvalidProductException("Error: Invalid product");
        }
        // verify Images input is not empty
        if (images == null || images.isEmpty()) {
            throw new EmptyImagesException("Error: Empty images");
        }

        int userId = userService.findUserIdByReference(reference);

        // verify product name is not used before
        if (productRepository.existsByUserIdAndName(userId, model.getName())) {
            throw new UsedProductNameException("Error: product name is already used");
        }

        List<String> imageUrls = imageService.save(userId, model.getName(), images, path);

        if (imageUrls.isEmpty()) {
            throw new ImageException("Error: Failed to save images");
        }

        Product product = productBuilder(userId, model, imageUrls);

        product = productRepository.save(product);

        return convertToProductViewModel(product);
    }

    @Override
    public List<ProductViewModel> getProductsBySearch(String search) {

        List<Product> products = productRepository.findByNameContaining(search);
        if (products != null) {
            List<ProductViewModel> productsViewModels = new ArrayList<ProductViewModel>();
            for (Product product : products) {
                productsViewModels.add(convertToProductViewModel(product));
            }
            return productsViewModels;
        } else {
            return null;
        }
    }

    @Override
    public List<ProductViewModel> getProductsByReference(String reference) {
        int userId = userService.findUserIdByReference(reference);

        List<Product> products = productRepository.findByUserId(userId);
        if (products != null) {
            List<ProductViewModel> productsViewModels = new ArrayList<ProductViewModel>();
            for (Product product : products) {
                productsViewModels.add(convertToProductViewModel(product));
            }
            return productsViewModels;
        } else {
            return null;
        }
    }

    @Override
    public List<ProductViewModel> getProductsByStoreName(String storeName) {
        int userId = sellerService.findUserIdByStoreName(storeName);

        if (userId == 0) {
            throw new InvalidStoreNameException("Error: store name " + storeName + " not found");
        }

        List<Product> products = productRepository.findByUserId(userId);
        if (products != null) {
            List<ProductViewModel> productsViewModels = new ArrayList<ProductViewModel>();
            for (Product product : products) {
                productsViewModels.add(convertToProductViewModel(product));
            }
            return productsViewModels;
        } else {
            return null;
        }
    }

    @Override
    public ProductViewModel update(String reference, ProductUpdateModel model, List<MultipartFile> images) {
        int userId = userService.findUserIdByReference(reference);
        Boolean isUpdateAvailable = false;
        Optional<Product> optionalProduct = productRepository.findById(model.getId());

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getUserId() != userId) {
                throw new UnAuthorizedUserException("Access denied: you are not allowed to access this product");
            }

            if (!model.getDescription().isEmpty()) {
                isUpdateAvailable = true;
                product.setDescription(model.getDescription());
            }
            if (model.getPrice() > 0) {
                isUpdateAvailable = true;
                product.setPrice(model.getPrice());
            }
            if (model.getStock() > 0) {
                isUpdateAvailable = true;
                product.setStock(model.getStock());
            }
            if (!model.getCategory().isEmpty()) {
                isUpdateAvailable = true;
                int categoryId = findIdByCategory(model.getCategory());
                if (categoryId == 0) {
                    throw new InvalidProductCategoryException("Error: Invalid product category");
                }
                product.setCategoryId(categoryId);
            }
            if (model.isActive() != product.isActive()) {
                isUpdateAvailable = true;
                product.setActive(model.isActive());
            }

            if (!model.getName().isEmpty()) {
                isUpdateAvailable = true;
                // Check if the new product name is already in use
                if (productRepository.existsByUserIdAndName(userId, model.getName())) {
                    throw new UsedProductNameException("Error: product name is already used");
                }
                // if images are present, add them to the list
                if (images != null && !images.isEmpty()) {
                    List<String> imageUrls = imageService.update(userId, product.getName(), model.getName(), images,
                            path);
                    if (imageUrls.isEmpty()) {
                        throw new ImageException("Error: Failed to save images");
                    }
                    product = updateImageUrls(product, imageUrls);
                }
                // else just update the Image Directory name
                else {
                    List<String> imageUrls = imageService.renameDirectory(userId, product.getName(), model.getName(),
                            path);
                    product = updateImageUrls(product, imageUrls);
                }
                product.setName(model.getName());
            } else {
                // if name is same but images are to be updated, add them to the list
                if (images != null && !images.isEmpty()) {
                    isUpdateAvailable = true;
                    List<String> imageUrls = imageService.update(userId, product.getName(), product.getName(), images,
                            path);
                    if (imageUrls.isEmpty()) {
                        throw new ImageException("Error: Failed to save images");
                    }
                    product = updateImageUrls(product, imageUrls);
                }
            }

            // save updated product information
            if (isUpdateAvailable) {
                product = productRepository.save(product);
            }

            return convertToProductViewModel(product);
        } else {
            throw new InvalidProductIdException("Error: Product with id " + model.getId() + " does not exist");
        }
    }

    @Override
    public ProductViewModel deactivate(String reference, int productId) {
        int userId = userService.findUserIdByReference(reference);
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getUserId() != userId) {
                throw new UnAuthorizedUserException("Access denied: you are not allowed to access this product");
            }
            product.setActive(false);
            product = productRepository.save(product);
            return convertToProductViewModel(product);
        } else {
            throw new InvalidProductIdException("Error: Product with id " + productId + " does not exist");
        }
    }

    @Override
    public ProductViewModel activate(String reference, int productId) {
        int userId = userService.findUserIdByReference(reference);
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getUserId() != userId) {
                throw new UnAuthorizedUserException("Access denied: you are not allowed to access this product");
            }
            product.setActive(true);
            product = productRepository.save(product);
            return convertToProductViewModel(product);
        } else {
            throw new InvalidProductIdException("Error: Product with id " + productId + " does not exist");
        }
    }

    // ----------------------------------------------------------------
    // HELPER FUNCTIONS FOR PRODUCTS
    // ----------------------------------------------------------------
    private Product updateImageUrls(Product product, List<String> imageUrls) {
        int imageUrlsSize = imageUrls.size();
        for (int i = imageUrlsSize; i < 9; i++) {
            imageUrls.add(null);
        }
        product.setImage1(imageUrls.get(0));
        product.setImage2(imageUrls.get(1));
        product.setImage3(imageUrls.get(2));
        product.setImage4(imageUrls.get(3));
        product.setImage5(imageUrls.get(4));
        product.setImage6(imageUrls.get(5));
        product.setImage7(imageUrls.get(6));
        product.setImage8(imageUrls.get(7));
        product.setImage9(imageUrls.get(8));

        return product;
    }

    public Product productBuilder(int userId, ProductInputModel model, List<String> imageUrls) {
        int imageUrlsSize = imageUrls.size();
        for (int i = imageUrlsSize; i < 9; i++) {
            imageUrls.add(null);
        }
        int categoryId = findIdByCategory(model.getCategory());
        if (categoryId == 0) {
            throw new InvalidProductCategoryException("Error: Invalid product category");
        }

        Product product = Product.builder()
                .userId(userId)
                .name(model.getName())
                .description(model.getDescription())
                .price(model.getPrice())
                .stock(model.getStock())
                .categoryId(categoryId)
                .active(model.isActive())
                .image1(imageUrls.get(0))
                .image2(imageUrls.get(1))
                .image3(imageUrls.get(2))
                .image4(imageUrls.get(3))
                .image5(imageUrls.get(4))
                .image6(imageUrls.get(5))
                .image7(imageUrls.get(6))
                .image8(imageUrls.get(7))
                .image9(imageUrls.get(8))
                .build();

        return product;
    }

    public Boolean verify(ProductInputModel product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            return false;
        } else if (product.getDescription() == null || product.getDescription().isEmpty()) {
            return false;
        } else if (product.getPrice() < 1) {
            return false;
        } else if (product.getStock() <= 1) {
            return false;
        } else if (product.getCategory() == null || product.getCategory().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private ProductViewModel convertToProductViewModel(Product product) {
        Optional<ProductCategory> productCategory = categoryRepository.findById(product.getCategoryId());
        if (productCategory.isPresent()) {
            return new ProductViewModel(product.getId(), product.getName(), product.getDescription(),
                    product.getPrice(),
                    product.getStock(), productCategory.get().getCategory(), product.isActive(), product.getImage1(),
                    product.getImage2(),
                    product.getImage3(),
                    product.getImage4(), product.getImage5(), product.getImage6(), product.getImage7(),
                    product.getImage8(), product.getImage9());
        } else {
            throw new InvalidProductCategoryException("Error: Invalid product category");
        }
    }

    @Override
    public int findIdByCategory(String category) {
        return categoryRepository.findIdByCategory(category).orElse(0);
    }

    @Override
    public Double findPriceById(int id) {
        return productRepository.findPriceById(id).orElse(0.0);
    }

    @Override
    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean existsById(int id) {
        return productRepository.existsById(id);
    }

    @Override
    public List<ProductIdAndNameModel> findProductIdAndNameByUserId(int userId) {
        return productRepository.findProductIdAndNameByUserId(userId);
    }

    @Override
    public int findUserIdByProductId(int productId) {
        return productRepository.findUserIdByProductId(productId).orElse(0);
    }

}