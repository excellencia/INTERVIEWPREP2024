import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CouponFinderWithDateFilter {

    private static Map<String, String> precomputedCouponMap = new HashMap<>();

    public static void precomputeCoupons(List<Map<String, String>> coupons, List<Map<String, String>> categories) throws ParseException {
        // Step 1: Build lookup maps
        Map<String, String> categoryMap = new HashMap<>();
        for (Map<String, String> category : categories) {
            categoryMap.put(category.get("CategoryName"), category.get("CategoryParentName"));
        }

        // Filter and keep only the latest valid coupon for each category
        Map<String, String> couponMap = getLatestValidCoupons(coupons);

        // Step 2: Precompute the coupon for every category
        for (String category : categoryMap.keySet()) {
            computeCouponForCategory(category, couponMap, categoryMap);
        }
    }

    private static Map<String, String> getLatestValidCoupons(List<Map<String, String>> coupons) throws ParseException {
        Map<String, Map<String, String>> latestCouponMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();

        for (Map<String, String> coupon : coupons) {
            String categoryName = coupon.get("CategoryName");
            String couponName = coupon.get("CouponName");
            Date dateModified = dateFormat.parse(coupon.get("DateModified"));

            // Ignore future-dated coupons
            if (dateModified.after(now)) {
                continue;
            }

            // Check if this is the latest coupon for the category
            if (!latestCouponMap.containsKey(categoryName) || dateModified.after(dateFormat.parse(latestCouponMap.get(categoryName).get("DateModified")))) {
                latestCouponMap.put(categoryName, coupon);
            }
        }

        // Convert the latestCouponMap into a simpler map with only CategoryName -> CouponName
        Map<String, String> couponMap = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : latestCouponMap.entrySet()) {
            couponMap.put(entry.getKey(), entry.getValue().get("CouponName"));
        }

        return couponMap;
    }

    private static void computeCouponForCategory(String categoryName, Map<String, String> couponMap, Map<String, String> categoryMap) {
        // If already precomputed, skip further processing
        if (precomputedCouponMap.containsKey(categoryName)) {
            return;
        }

        // Traverse the hierarchy to find the coupon
        String currentCategory = categoryName;
        while (currentCategory != null) {
            if (couponMap.containsKey(currentCategory)) {
                precomputedCouponMap.put(categoryName, couponMap.get(currentCategory));
                return;
            }
            currentCategory = categoryMap.get(currentCategory); // Move to the parent
        }

        // No coupon found in the hierarchy
        precomputedCouponMap.put(categoryName, null);
    }

    public static String findBestCoupon(String categoryName) {
        // Lookup the precomputed result
        return precomputedCouponMap.getOrDefault(categoryName, null);
    }

    public static void main(String[] args) throws ParseException {
        // Example data: Coupons
        List<Map<String, String>> coupons = Arrays.asList(
            Map.of("CategoryName", "Comforter Sets", "CouponName", "Comforters Sale", "DateModified", "2020-01-01"),
            Map.of("CategoryName", "Comforter Sets", "CouponName", "Cozy Comforter Coupon", "DateModified", "2020-01-02"),
            Map.of("CategoryName", "Bedding", "CouponName", "Savings on Bedding", "DateModified", "2019-01-01"),
            Map.of("CategoryName", "Bedding", "CouponName", "Best Bedding Bargains", "DateModified", "2019-02-01"),
            Map.of("CategoryName", "Bed & Bath", "CouponName", "Low price for Bed & Bath", "DateModified", "2018-01-01"),
            Map.of("CategoryName", "Bed & Bath", "CouponName", "Bed & Bath extravaganza", "DateModified", "2019-01-01"),
            Map.of("CategoryName", "Bed & Bath", "CouponName", "Future Coupon", "DateModified", "2030-01-01")
        );

        // Example data: Categories
        List<Map<String, String>> categories = Arrays.asList(
            Map.of("CategoryName", "Comforter Sets", "CategoryParentName", "Bedding"),
            Map.of("CategoryName", "Bedding", "CategoryParentName", "Bed & Bath"),
            //Map.of("CategoryName", "Bed & Bath", "CategoryParentName", null),
            Map.of("CategoryName", "Soap Dispensers", "CategoryParentName", "Bathroom Accessories"),
            Map.of("CategoryName", "Bathroom Accessories", "CategoryParentName", "Bed & Bath"),
            Map.of("CategoryName", "Toy Organizers", "CategoryParentName", "Baby And Kids")
            //Map.of("CategoryName", "Baby And Kids", "CategoryParentName", null)
        );

        // Precompute all coupons
        precomputeCoupons(coupons, categories);

        // Test Cases
        System.out.println(findBestCoupon("Comforter Sets"));        // Output: Cozy Comforter Coupon
        System.out.println(findBestCoupon("Bedding"));               // Output: Best Bedding Bargains
        System.out.println(findBestCoupon("Bathroom Accessories"));  // Output: Bed & Bath extravaganza
        System.out.println(findBestCoupon("Soap Dispensers"));       // Output: Bed & Bath extravaganza
        System.out.println(findBestCoupon("Toy Organizers"));        // Output: null
    }
}

