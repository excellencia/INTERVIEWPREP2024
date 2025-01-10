/* initialize coupons and Categories
getLatestValidCoupon
computeCouponForCategory
findBestCoupon*/
import java.util.*;
import java.util.stream.*;
import java.time.LocalDate;

public class CouponFinderWithDateFilter1{
  private final Map<String, String> precomputedCouponMap = new HashMap<>();

  public CouponFinderWithDateFilter1(List<Map<String, String>> coupons, List<Map<String, String>> categories) {
    Objects.requireNonNull(coupons, "Coupons list cannot be null"); 
    Objects.requireNonNull(categories, "Categories list cannot be null");
    initializeCoupons(coupons, categories);
  }

  private void initializeCoupons(List<Map<String, String>> coupons, List<Map<String, String>> categories) {
  // Build category parent relationships using HashMap instead of Collectors.toMap()
  Map<String, String> categoryMap = new HashMap<>();
  for (Map<String, String> category : categories) {
      categoryMap.put(
          category.get("CategoryName"),
          category.get("CategoryParentName")  // This can safely be null now
      );
  }

    Map<String, String> couponMap = getLatestValidCoupon(coupons);

    for(String category : categoryMap.keySet()){
        computeCouponForCategory(category, couponMap, categoryMap);
    }
    }

  private Map<String, String> getLatestValidCoupon(List<Map<String, String>> coupons) {
    LocalDate now = LocalDate.now();

    Comparator<Map<String, String>> couponComparator = (c1, c2) -> {
      LocalDate date1 = LocalDate.parse(c1.get("DateModified"));
      LocalDate date2 = LocalDate.parse(c2.get("DateModified"));
      int dateComparison = date1.compareTo(date2);
      //if dates are equal, use coupon name for determinstic tie-breaking
      return dateComparison != 0 ? dateComparison :
              c1.get("CouponName").compareTo(c2.get("CouponName"));
    };

    return coupons.stream()
      .filter(coupon -> {
        LocalDate couponDate = LocalDate.parse(coupon.get("DateModified"));
        return !couponDate.isAfter(now);
      })
      .collect(Collectors.groupingBy(
        coupon -> coupon.get("CategoryName"),
        Collectors.collectingAndThen(
          Collectors.maxBy(couponComparator),
          optCoupon -> optCoupon.map(c -> c.get("CouponName")).orElse(null)
        )
      ));
  }

  private void computeCouponForCategory(String categoryName,Map<String, String> couponMap, Map<String, String> categoryMap) {
    if(precomputedCouponMap.containsKey(categoryName)) {
        return;
    }
    boolean foundCoupon = false;
    String currentCategory = categoryName;
    while(currentCategory != null) {
      if(couponMap.containsKey(currentCategory)) {
        precomputedCouponMap.put(categoryName, couponMap.get(currentCategory));
        foundCoupon = true;
        break;
      }
      currentCategory = categoryMap.get(currentCategory);
    }
    if (!foundCoupon) {
      precomputedCouponMap.put(categoryName, null);
    }
    }
  public String findBestCoupon(String category) {
      return precomputedCouponMap.getOrDefault(category, null);
  }

   public static void main(String[] args) {
       // Test data setup
       List<Map<String, String>> coupons = Arrays.asList(
        createMap("CategoryName", "Comforter Sets", "CouponName", "Comforters Sale", "DateModified", "2020-01-01"),
        createMap("CategoryName", "Comforter Sets", "CouponName", "Cozy Comforter Coupon", "DateModified", "2020-01-02"),
        createMap("CategoryName", "Bedding", "CouponName", "Savings on Bedding", "DateModified", "2019-01-01"),
        createMap("CategoryName", "Bedding", "CouponName", "Best Bedding Bargains", "DateModified", "2019-02-01"),
        createMap("CategoryName", "Bed & Bath", "CouponName", "Low price for Bed & Bath", "DateModified", "2018-01-01"),
        createMap("CategoryName", "Bed & Bath", "CouponName", "Bed & Bath extravaganza", "DateModified", "2019-01-01"),
        createMap("CategoryName", "Bed & Bath", "CouponName", "Future Coupon", "DateModified", "2030-01-01")
    );

    // Example data: Categories
    List<Map<String, String>> categories = Arrays.asList(
        createMap("CategoryName", "Comforter Sets", "CategoryParentName", "Bedding"),
        createMap("CategoryName", "Bedding", "CategoryParentName", "Bed & Bath"),
        createMap("CategoryName", "Bed & Bath", "CategoryParentName", null),
        createMap("CategoryName", "Soap Dispensers", "CategoryParentName", "Bathroom Accessories"),
        createMap("CategoryName", "Bathroom Accessories", "CategoryParentName", "Bed & Bath"),
        createMap("CategoryName", "Toy Organizers", "CategoryParentName", "Baby And Kids"),
        createMap("CategoryName", "Baby And Kids", "CategoryParentName", null)
    );

       // Create finder instance and run tests
       CouponFinderWithDateFilter1 finder = new CouponFinderWithDateFilter1(coupons, categories);

       // Test different scenarios
       System.out.println("Test Results:");
       
       // Test case for "Comforter Sets": Should show the most recent coupon for "Comforter Sets"
       System.out.println("Comforter Sets: " + finder.findBestCoupon("Comforter Sets"));
       
       // Test case for "Bedding": Should show the most recent coupon for "Bedding"
       System.out.println("Bedding: " + finder.findBestCoupon("Bedding"));
       
       // Test case for "Bathroom Accessories": Should inherit the coupon from "Bed & Bath"
         System.out.println("Bathroom Accessories: " + finder.findBestCoupon("Bathroom Accessories"));
     }
  
      private static Map<String, String> createMap(String... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of entries. Entries should be in pairs.");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            map.put(entries[i], entries[i + 1]);
        }
        return map;
    }
}
