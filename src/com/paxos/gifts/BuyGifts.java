package com.paxos.gifts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class BuyGifts {
    static class Item {
        private final String _name;
        private final int _price;

        Item(String name, int price) {
            _name = name;
            _price = price;
        }

        public int getPrice() {
            return _price;
        }

        public String getName() {
            return _name;
        }

        public String toString() {
            return _name + " " + _price;
        }
    }

    private static List<Item> buyItems(List<Item> allItems, int budget) {
        Map<Integer, List<Item>> priceMap = allItems.stream().collect(Collectors.groupingBy(Item::getPrice));
        List<Item> result = new ArrayList<>();

        // Ideal case where two different items add up to budget.
        // Runtime is O(n)
        for (Item item : allItems) {
            int targetP = budget - item.getPrice();
            if (priceMap.containsKey(targetP)) {
                List<Item> targets = priceMap.get(targetP);
                for (Item t : targets) {
                    if (t != item) {
                        result.add(item);
                        result.add(t);
                        return result;
                    }
                }
            }
        }

        // No two items add up to budget. We need to find two items whose sum is closest budget.
        // The idea is as followsL
        // 1. Sort the list of items by price in ascending order
        // 2. Have two pointers that start from both ends of the list of items: low and high
        // 3. If the sum of the two prices is greater than budget, decrease the high pointer by 1, otherwise increase
        //    the low pointer by 1
        // 4. If the sum is less than budget, compute the difference between sum and budget, and keep track of the overall
        //    smallest difference. We don't need to do this if the sum is larger than budget since we can't buy anything.
        // 5. The two items that have the smallest difference will be our answer.
        //
        // Runtime is O(n*logn) since sorting is nlogn
        allItems.sort(Comparator.comparingInt(Item::getPrice));
        int low = 0, high = allItems.size() - 1;
        int diff = Integer.MAX_VALUE;
        Item a = null, b = null;
        while (low < high) {
            int sum = allItems.get(low).getPrice() + allItems.get(high).getPrice();
            if (sum > budget) {
                high--;
            } else {
                int currDiff = budget - sum;
                if (currDiff < diff) {
                    a = allItems.get(low);
                    b = allItems.get(high);
                    diff = currDiff;
                }
                low++;
            }
        }
        if (a != null && b != null) {
            result.add(a);
            result.add(b);
        }

        return result;
    }

    public static List<Item> buyFromFile(String filePath, int budget) throws FileNotFoundException {
        List<Item> allItems = new LinkedList<>();
        Scanner scanner = new Scanner(new File(filePath));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split(",");
            allItems.add(new Item(tokens[0].trim(), Integer.parseInt(tokens[1].trim())));
        }
        return buyItems(allItems, budget);
    }

    public static List<Item> buyFromString(String input, int budget) {
        List<Item> allItems = new LinkedList<>();
        String[] lines = input.split("\n");
        for (String line : lines) {
            String[] tokens = line.split(",");
            allItems.add(new Item(tokens[0].trim(), Integer.parseInt(tokens[1].trim())));
        }
        return buyItems(allItems, budget);
    }

    public static void main(String[] args) {
        String input = "Candy Bar, 500\n" +
                "Paperback Book, 700\n" +
                "Detergent, 1000\n" +
                "Headphones, 1400\n" +
                "Earmuffs, 2000\n" +
                "Bluetooth Stereo, 6000";
        int budget = 1100;

        List<Item> result = null;
        if (args.length > 1) {
            try {
                result = BuyGifts.buyFromFile(args[0], Integer.parseInt(args[1]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            result = BuyGifts.buyFromString(input, budget);
        }

        if (result == null || result.isEmpty()) {
            System.out.println("Not possible");
        } else {
            result.stream().forEach(i -> System.out.println(i));
        }
    }
}
