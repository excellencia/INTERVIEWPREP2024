public class StringAddition {
    public static String addStrings(String num1, String num2) {
      if(num1.equals("0")) return num2;
      if(num2.equals("0")) return num1;
  
      int maxLength = Math.max(num1.length(), num2.length());
  
      num1 = padLeft(num1, maxLength);
      num2 = padLeft(num2, maxLength);
  
      StringBuilder result = new StringBuilder();
      int carry = 0;
  
      for(int i = maxLength - 1; i >= 0; i--) {
        int digit1 = num1.charAt(i) - '0';
        int digit2 = num2.charAt(i) - '0';
  
        int sum = digit1 + digit2 + carry;
  
        carry = sum / 10;
        int currentDigit = sum % 10;
  
        result.insert(0, currentDigit);
      }
      if(carry > 0) {
        result.insert(0, carry);
      }
      return result.toString();
    }
  
    private static String padLeft(String str, int length) {
      if(str.length() >= length) return str;
  
      StringBuilder sb = new StringBuilder();
      while(sb.length() < length - str.length()) {
        sb.append('0');
      }
      sb.append(str);
      return sb.toString();
    }
    public static void main(String[] args) {
          // Test cases
          System.out.println(addStrings("1234", "4321")); // "5555"
          System.out.println(addStrings("999", "1")); // "1000"
          System.out.println(addStrings("0", "123")); // "123"
          System.out.println(addStrings("456", "789")); // "1245"
          System.out.println(addStrings("11", "123")); // "134"
          System.out.println(addStrings("999999", "1")); // "1000000"
      }
  }
  