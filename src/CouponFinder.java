import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;

public class CouponFinder {
    private final Map<String, String> categoryMap = new HashMap<>();
    private final Map<String, Set<String>> precomputedCouponMap = new HashMap<>();
    private final Map<String, String> couponToDiscountMap = new HashMap<>();
    private final Map<String, Map<String, String>> productMap = new HashMap<>();

    public CouponFinder(List<Map<String, String>> coupons, List<Map<String, String>> categories,
            List<Map<String, String>> products) {
        initializeMaps(coupons, categories, products);
    }

    private void initializeMaps(List<Map<String, String>> coupons, List<Map<String, String>> categories,
            List<Map<String, String>> products) {

        Map<String, Set<String>> couponMap = getLatestValidCoupon(coupons);
        for (Map<String, String> category : categories) {
            categoryMap.put(category.get("CategoryName"), category.get("CategoryParentName"));
        }
        for (Map<String, String> product : products) {
            productMap.put(product.get("ProductName"), product);
        }
        System.out.println("Product Map size is: " + productMap.size());
        for (Map<String, String> coupon : coupons) {
            couponToDiscountMap.put(coupon.get("CouponName"), coupon.get("Discount"));
        }

        for (String category : categoryMap.keySet()) {
            computeCouponForCategory(category, couponMap, categoryMap);
        }
    }

    private Map<String, Set<String>> getLatestValidCoupon(List<Map<String, String>> coupons) {
        Map<String, Map<LocalDate, Set<String>>> categoryDateCoupons = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (Map<String, String> coupon : coupons) {
            String categoryName = coupon.get("CategoryName");
            LocalDate couponDate = LocalDate.parse(coupon.get("DateModified"));

            if (couponDate.isAfter(now))
                continue;

            categoryDateCoupons.computeIfAbsent(categoryName, k -> new HashMap<>())
                    .computeIfAbsent(couponDate, k -> new TreeSet<>()).add(coupon.get("CouponName"));

        }

        Map<String, Set<String>> resultMap = new HashMap<>();
        for (var entry : categoryDateCoupons.entrySet()) {
            String categoryName = entry.getKey();
            Map<LocalDate, Set<String>> dateCoupons = entry.getValue();
            LocalDate mostRecentDate = dateCoupons.keySet().stream().max(LocalDate::compareTo).orElse(null);
            if (mostRecentDate != null) {
                resultMap.put(categoryName, dateCoupons.get(mostRecentDate));
            }
        }
        return resultMap;
    }

    private void computeCouponForCategory(String categoryName, Map<String, Set<String>> coupons,
            Map<String, String> categories) {
        if (precomputedCouponMap.containsKey(categoryName))
            return;
        String currentCategory = categoryName;
        while (currentCategory != null) {
            if (coupons.containsKey(currentCategory)) {
                precomputedCouponMap.put(categoryName, coupons.get(currentCategory));
                return;
            }
            currentCategory = categoryMap.get(currentCategory);
        }
        precomputedCouponMap.put(categoryName, null);

    }

    public String calculateDiscountedPrice(String productName) {
        System.out.println("Product Map size is: " + productMap.size());
        Map<String, String> product = productMap.get(productName);
        if (product == null)
            return null;

        String categoryName = product.get("CategoryName");
        BigDecimal originalPrice = new BigDecimal(product.get("Price"));

        Set<String> coupons = precomputedCouponMap.get(categoryName);
        if (coupons == null || coupons.isEmpty())
            return originalPrice.toString();

        Set<String> discountedPrices = new TreeSet<>();
        for (String couponName : coupons) {
            String discount = couponToDiscountMap.get(couponName);
            BigDecimal discountedPrice = calculateDiscount(originalPrice, discount);
            discountedPrices.add(discountedPrice.setScale(2, RoundingMode.HALF_UP).toString());
        }
        return String.join(" OR ", discountedPrices);
    }

    private BigDecimal calculateDiscount(BigDecimal price, String discount) {
        if (discount.endsWith("%")) {
            BigDecimal percentage = new BigDecimal(discount.substring(0, discount.length() - 1))
                    .divide(BigDecimal.valueOf(100));
            return price.subtract(price.multiply(percentage));
        } else {
            BigDecimal amount = new BigDecimal(discount.substring(1));
            return price.subtract(amount);
        }
    }

    public static void main(String[] args) {
        // Test data setup
        List<Map<String, String>> coupons = Arrays.asList(
                // First test case: Multiple coupons with same date (should show multiple
                // prices)
                createMap("CategoryName", "Comforter Sets", "CouponName", "Comforters Sale", "DateModified",
                        "2020-01-01", "Discount", "10%"),
                createMap("CategoryName", "Comforter Sets", "CouponName", "Cozy Comforter Coupon", "DateModified",
                        "2020-01-01", "Discount", "$15"),

                // Second test case: Two coupons with same date but different discount types
                createMap("CategoryName", "Bedding", "CouponName", "Best Bedding Bargains", "DateModified",
                        "2019-01-01", "Discount", "35%"),
                createMap("CategoryName", "Bedding", "CouponName", "Savings on Bedding", "DateModified", "2019-01-01",
                        "Discount", "25%"),

                // Third test case: Multiple coupons with different dates (should pick most
                // recent)
                createMap("CategoryName", "Bed & Bath", "CouponName", "Low price for Bed & Bath", "DateModified",
                        "2018-01-01", "Discount", "50%"),
                createMap("CategoryName", "Bed & Bath", "CouponName", "Bed & Bath extravaganza", "DateModified",
                        "2019-01-01", "Discount", "75%"),

                // Add a future dated coupon (should be ignored)
                createMap("CategoryName", "Bed & Bath", "CouponName", "Future Sale", "DateModified", "2030-01-01",
                        "Discount", "90%"));

        List<Map<String, String>> categories = Arrays.asList(
                createMap("CategoryName", "Comforter Sets", "CategoryParentName", "Bedding"),
                createMap("CategoryName", "Bedding", "CategoryParentName", "Bed & Bath"),
                createMap("CategoryName", "Bed & Bath", "CategoryParentName", null),
                createMap("CategoryName", "Soap Dispensers", "CategoryParentName", "Bathroom Accessories"),
                createMap("CategoryName", "Bathroom Accessories", "CategoryParentName", "Bed & Bath"),
                createMap("CategoryName", "Baby And Kids", "CategoryParentName", null));

        List<Map<String, String>> products = Arrays.asList(
                // Test case 1: Product with multiple valid coupons
                createMap("ProductName", "Cozy Comforter", "Price", "100.00", "CategoryName", "Comforter Sets"),

                // Test case 2: Product with inherited coupon
                createMap("ProductName", "Infinite Soap Dispenser", "Price", "500.00", "CategoryName",
                        "Bathroom Accessories"),

                // Test case 3: Product with no applicable coupons
                createMap("ProductName", "Rainbow Toy Box", "Price", "257.00", "CategoryName", "Baby And Kids"),

                // Test case 4: Product with percentage-based discounts
                createMap("ProductName", "All-in-one Bedding Set", "Price", "50.00", "CategoryName", "Bedding"));

        CouponFinder finder = new CouponFinder(coupons, categories, products);

        // Test Case 1: Multiple coupons with same date
        System.out.println("Test Case 1 - Multiple coupons same date:");
        System.out.println("Cozy Comforter ($100.00) => " + finder.calculateDiscountedPrice("Cozy Comforter"));
        // Expected: Should show both "90.00" (10% off) OR "85.00" ($15 off)

        // Test Case 2: Inherited coupon through category hierarchy
        System.out.println("\nTest Case 2 - Inherited coupon:");
        System.out.println(
                "Infinite Soap Dispenser ($500.00) => " + finder.calculateDiscountedPrice("Infinite Soap Dispenser"));
        // Expected: Should show "125.00" (75% off from Bed & Bath's most recent coupon)

        // Test Case 3: No applicable coupons
        System.out.println("\nTest Case 3 - No applicable coupons:");
        System.out.println("Rainbow Toy Box ($257.00) => " + finder.calculateDiscountedPrice("Rainbow Toy Box"));
        // Expected: Should show original price "257.00"

        // Test Case 4: Multiple percentage discounts
        System.out.println("\nTest Case 4 - Multiple percentage discounts:");
        System.out.println(
                "All-in-one Bedding Set ($50.00) => " + finder.calculateDiscountedPrice("All-in-one Bedding Set"));
        // Expected: Should show "32.50" (35% off) OR "37.50" (25% off)

        // Test Case 5: Non-existent product
        System.out.println("\nTest Case 5 - Non-existent product:");
        System.out.println("Non-existent Product => " + finder.calculateDiscountedPrice("Non-existent Product"));
        // Expected: Should return null
    }

    private static Map<String, String> createMap(String... entries) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put(entries[i], entries[i + 1]);
        }
        return map;
    }
}
