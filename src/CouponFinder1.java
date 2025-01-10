import java.util.*;
import java.util.stream.Collectors;
public class CouponFinder1 {

    private static Map<String, String> precomputedCoupons = new HashMap<>();

    public static void precomputeCoupons(List<Map<String, String>> coupons, List<Map<String, String>> categories) 
    {
        Map<String, String> couponMap = coupons.stream()
                    .collect(Collectors.toMap(coupon -> coupon.get("CategoryName"), coupon -> coupon.get("CouponName")));
        Map<String, String> categoryMap = categories.stream()
                .collect(Collectors.toMap(category -> category.get("CategoryName"), category -> category.get("CategoryParentName")));

        for(String category : categoryMap.keySet()) {
            computeCouponForCategory(category, couponMap, categoryMap);
        }
    }
    
    private static void computeCouponForCategory(String categoryName, Map<String, String> couponMap, Map<String, String> categoryMap) {
        if (precomputedCoupons.containsKey(categoryName)) {
            return;
        }
        String currentCategory = categoryName;
        while (currentCategory != null) {   
            if (couponMap.containsKey(currentCategory)) {
                precomputedCoupons.put(categoryName, couponMap.get(currentCategory));
                return;
            }
            currentCategory = categoryMap.get(currentCategory);
        }
        precomputedCoupons.put(categoryName, null);
    }

    public static String findBestCoupon(String categoryName) {
        // Lookup the precomputed result
        return precomputedCoupons.get(categoryName);
    }
    
    public static void main(String[] args) {
        // Example data: Coupons
        List<Map<String, String>> coupons = Arrays.asList(
            Map.of("CategoryName", "Comforter Sets", "CouponName", "Comforters Sale"),
            Map.of("CategoryName", "Bedding", "CouponName", "Savings on Bedding"),
            Map.of("CategoryName", "Bed & Bath", "CouponName", "Low price for Bed & Bath")
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
        System.out.println(findBestCoupon("Comforter Sets"));        // Output: Comforters Sale
        System.out.println(findBestCoupon("Bedding"));               // Output: Savings on Bedding
        System.out.println(findBestCoupon("Bathroom Accessories"));  // Output: Low price for Bed & Bath
        System.out.println(findBestCoupon("Soap Dispensers"));       // Output: Low price for Bed & Bath
        System.out.println(findBestCoupon("Toy Organizers"));        // Output: null
    }
}
