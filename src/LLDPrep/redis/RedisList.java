package LLDPrep.redis;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.ArrayList;

public class RedisList {
    private final Deque<String> list;

    // Constructor
    public RedisList() {
        this.list = new ArrayDeque<>();
    }

    // LPUSH: Add an element to the head of the list
    public void lpush(String value) {
        list.addFirst(value);
    }

    // RPUSH: Add an element to the tail of the list
    public void rpush(String value) {
        list.addLast(value);
    }

    // LPOP: Remove and return the head element
    public String lpop() {
        return list.pollFirst(); // Returns null if the list is empty
    }

    // RPOP: Remove and return the tail element
    public String rpop() {
        return list.pollLast(); // Returns null if the list is empty
    }

    // LRANGE: Get elements from the list within a range
    public List<String> lrange(int start, int end) {
        List<String> result = new ArrayList<>(list);
        if (start < 0) start = result.size() + start; // Handle negative indexing
        if (end < 0) end = result.size() + end; // Handle negative indexing
        start = Math.max(0, start); // Clamp to the beginning
        end = Math.min(result.size() - 1, end); // Clamp to the end
        if (start > end) return new ArrayList<>(); // Return empty list if range is invalid
        return result.subList(start, end + 1);
    }

    // Debugging: Print the entire list
    public void printList() {
        System.out.println(list);
    }

    // Main method for testing
    public static void main(String[] args) {
        RedisList redisList = new RedisList();

        // Test LPUSH and RPUSH
        redisList.lpush("A");
        redisList.lpush("B");
        redisList.rpush("C");
        redisList.rpush("D");

        redisList.printList(); // Expected: [B, A, C, D]

        // Test LPOP and RPOP
        System.out.println(redisList.lpop()); // Expected: B
        System.out.println(redisList.rpop()); // Expected: D

        redisList.printList(); // Expected: [A, C]

        // Test LRANGE
        redisList.lpush("X");
        redisList.rpush("Y");
        System.out.println(redisList.lrange(0, 2)); // Expected: [X, A, C]
        System.out.println(redisList.lrange(-2, -1)); // Expected: [C, Y]
    }
}
