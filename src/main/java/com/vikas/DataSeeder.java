package com.vikas;

import com.vikas.domain.AccountStatus;
import com.vikas.domain.USER_ROLE;
import com.vikas.model.Category;
import com.vikas.model.Product;
import com.vikas.model.Seller;
import com.vikas.repository.CategoryRepository;
import com.vikas.repository.ProductRepository;
import com.vikas.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Cleaning database for fresh seeding...");
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        System.out.println("Seeding default seller and products...");

        // 1. Create a default active seller
        Seller seller = sellerRepository.findByEmail("seller@neuralnoir.com");
        if (seller == null) {
            seller = new Seller();
            seller.setEmail("seller@neuralnoir.com");
            seller.setPassword(passwordEncoder.encode("Password@123"));
            seller.setSellerName("Neural Noir Official Store");
            seller.setMobile("9876543210");
            seller.setEmailVerified(true);
            seller.setAccountStatus(AccountStatus.ACTIVE);
            seller = sellerRepository.save(seller);
        }

        // 2. Seed categories and products
        // Men Topwear
        seedProduct(seller, "Men Classic Black T-Shirt", "A premium heavyweight cotton classic black t-shirt.", 1200.0, 799.0, "Black", "M", "men", "men_topwear", "men_t_shirts", 
            Arrays.asList("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800", "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800"));
        
        seedProduct(seller, "Men Slim Fit Casual Shirt", "Modern slim fit casual shirt perfect for semi-formal gatherings.", 2500.0, 1499.0, "White", "L", "men", "men_topwear", "men_casual_shirts",
            Arrays.asList("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=800"));
            
        seedProduct(seller, "Men Classic Leather Biker Jacket", "Authentic premium leather biker jacket with asymmetrical zipper.", 9500.0, 5499.0, "Dark Black", "L", "men", "men_topwear", "men_jackets",
            Arrays.asList("https://images.unsplash.com/photo-1551028719-00167b16eac5?w=800"));

        // Men Bottomwear
        seedProduct(seller, "Men Regular Fit Blue Jeans", "Classic straight-leg dark wash blue denim jeans.", 3200.0, 1799.0, "Blue", "32", "men", "men_bottomwear", "men_jeans",
            Arrays.asList("https://images.unsplash.com/photo-1542272604-787c3835535d?w=800"));

        seedProduct(seller, "Men Athletic Board Shorts", "Lightweight, quick-dry activewear athletic shorts.", 1500.0, 899.0, "Grey", "M", "men", "men_bottomwear", "men_shorts",
            Arrays.asList("https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=800"));

        // Men Footwear
        seedProduct(seller, "Men Premium Leather Loafers", "Sleek tan brown hand-crafted leather formal loafers.", 4500.0, 2999.0, "Brown", "9", "men", "men_footwear", "men_casual_shoes",
            Arrays.asList("https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800"));

        seedProduct(seller, "Men Retro Air Running Sneakers", "High-performance mesh-panel running sneakers.", 6000.0, 3999.0, "Multi", "10", "men", "men_footwear", "men_sneakers",
            Arrays.asList("https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800"));

        // Women Indian
        seedProduct(seller, "Women Floral Kurta Set", "Elegant cotton kurta set with dupatta.", 3800.0, 1999.0, "Pink", "S", "women", "women_indian_and_fusion_wear", "women_kurtas_and_suits",
            Arrays.asList("https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=800"));

        seedProduct(seller, "Women Banarasi Silk Saree", "Traditional heritage Banarasi silk saree with gold zari work.", 8500.0, 4999.0, "Red", "Free", "women", "women_indian_and_fusion_wear", "women_sarees",
            Arrays.asList("https://images.unsplash.com/photo-1610030469668-93535c17b6b3?w=800"));

        // Women Western
        seedProduct(seller, "Women Chic Linen Blouse Top", "Breathable relaxed summer top styled with wooden buttons.", 1800.0, 999.0, "Yellow", "M", "women", "women_western_wear", "women_tops",
            Arrays.asList("https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=800"));

        seedProduct(seller, "Women Elegant Satin Maxi Dress", "Glistening midnight black evening dress with cowl neckline.", 5000.0, 2999.0, "Black", "S", "women", "women_western_wear", "women_dresses",
            Arrays.asList("https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800"));

        // Electronics Mobiles
        seedProduct(seller, "Xiaomi Mi 14 Ultra", "The ultimate Leica-lens camera flagship smartphone.", 75000.0, 69999.0, "Black", "512GB", "electronics", "mobiles", "mi_mobile",
            Arrays.asList("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800"));

        seedProduct(seller, "Realme GT 6", "AI flagship phone with Snapdragon 8s Gen 3.", 45000.0, 39999.0, "Silver", "256GB", "electronics", "mobiles", "realme_mobile",
            Arrays.asList("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800"));

        seedProduct(seller, "Samsung Galaxy S24 Ultra", "Flagship smartphone with built-in S-Pen.", 125000.0, 119999.0, "Titanium", "256GB", "electronics", "mobiles", "samsung_mobile",
            Arrays.asList("https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800"));

        // Electronics Laptops
        seedProduct(seller, "ASUS ROG Zephyrus G14", "Ultrafast high-performance OLED gaming laptop.", 145000.0, 129999.0, "Grey", "16GB", "electronics", "laptops", "gaming_laptops",
            Arrays.asList("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800"));

        // Home & Furniture Flooring
        seedProduct(seller, "Handcrafted Persian Silk Carpet", "Premium hand-woven oriental area carpet with royal designs.", 15000.0, 8999.0, "Red", "6x9 Ft", "home_furniture", "flooring", "carpets",
            Arrays.asList("https://images.unsplash.com/photo-1600121848594-d8644e57abab?w=800"));

        // Home & Furniture Bed Linen
        seedProduct(seller, "Premium Egyptian Cotton Bedsheet", "Luxury thread count silky smooth king-size bedsheet.", 3500.0, 1999.0, "White", "King", "home_furniture", "bed_linen_furnishing", "bedsheets",
            Arrays.asList("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800"));

        // Home & Furniture Home Decor
        seedProduct(seller, "Modern Geometric Metal Wall Art", "Abstract sleek wall panel sculpture decoration.", 4999.0, 2499.0, "Gold", "Medium", "home_furniture", "home_decor", "wall_decor",
            Arrays.asList("https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=800"));

        System.out.println("Data seeding completed successfully!");
    }

    private void seedProduct(Seller seller, String title, String description, double mrp, double sellingPrice, 
                             String color, String size, String cat1Id, String cat2Id, String cat3Id, List<String> images) {
        
        // Ensure Level 1 Category
        Category category1 = categoryRepository.findByCategoryId(cat1Id);
        if (category1 == null) {
            category1 = new Category();
            category1.setCategoryId(cat1Id);
            category1.setName(capitalize(cat1Id.replace("_", " ")));
            category1.setLevel(1);
            category1 = categoryRepository.save(category1);
        }

        // Ensure Level 2 Category
        Category category2 = categoryRepository.findByCategoryId(cat2Id);
        if (category2 == null) {
            category2 = new Category();
            category2.setCategoryId(cat2Id);
            category2.setName(capitalize(cat2Id.replace("_", " ")));
            category2.setParentCategory(category1);
            category2.setLevel(2);
            category2 = categoryRepository.save(category2);
        }

        // Ensure Level 3 Category
        Category category3 = categoryRepository.findByCategoryId(cat3Id);
        if (category3 == null) {
            category3 = new Category();
            category3.setCategoryId(cat3Id);
            category3.setName(capitalize(cat3Id.replace("_", " ")));
            category3.setParentCategory(category2);
            category3.setLevel(3);
            category3 = categoryRepository.save(category3);
        }

        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setMrpPrice(mrp);
        product.setSellingPrice(sellingPrice);
        product.setColor(color);
        product.setSize(size);
        product.setImages(images);
        product.setCategory(category3);
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setNumRatings(0);
        
        double discount = mrp - sellingPrice;
        product.setDiscountPercentage((discount / mrp) * 100);

        productRepository.save(product);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                result.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }
}
