package org.example;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;
 
public class PhoneBook {
    private HashMap<String, Set<String>> phoneBook;
 
    public PhoneBook(String filename) throws IOException {
        phoneBook = new HashMap<>();
        loadFromFile(filename);
    }
 
    private void loadFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\s+");
            String phone = normalizePhone(parts[0]);
            String surname = parts[1];
            phoneBook.merge(surname, new HashSet<>(Arrays.asList(phone)), (prev, n) -> {
                prev.add(phone);
                return prev;
            });
        }
        reader.close();
    }
 
    private String normalizePhone(String phone) {
        phone = phone.replaceAll("[()-]", "");
        
        if (phone.length() == 11 && (phone.charAt(0) == '8' || phone.charAt(0) == '7')) {
            phone = "+7" + phone.substring(1);
        } else if (!phone.startsWith("+7")) {
            phone = "+7" + phone;
        }
        return phone;
    }
 
    public void findByLastName(String lastName) {
        Set<String> numbers = phoneBook.getOrDefault(lastName, Collections.emptySet());
        if (numbers.isEmpty()) {
            System.out.println("No entries found for the last name: " + lastName);
        } else {
            System.out.println("Numbers by surname " + lastName + ":");
            for (String num : numbers) {
                System.out.println(num);
            }
        }
    }
 
    public void findByLotteryNumber(int targetSum) {
        boolean found = false;
        for (String surname : phoneBook.keySet()) {
            for (String phone : phoneBook.get(surname)) {
                int sum = sumDigits(phone);
                if (sum == targetSum) {
                    System.out.println(phone + " " + surname);
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No entries found with the sum of digits equal to " + targetSum);
        }
    }
 
    private static int sumDigits(String phone) {
        int sum = 0;
        for (char c : phone.toCharArray()) {
            if (Character.isDigit(c)) {
                sum += Character.getNumericValue(c);
            }
        }
        return sum;
    }
 
    public static void main(String[] args) {
        try {
            PhoneBook phoneBook = new PhoneBook("phonebook.txt");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                int choice;
                System.out.println("Choose an option:");
                System.out.println("1. Find by last name");
                System.out.println("2. Find by lottery number");
                try {
                    choice = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid option, try again");
                    scanner.next();
                    continue;
                }
 
                if (choice == 1) {
                    System.out.println("Enter last name:");
                    String lastName = scanner.next();
                    phoneBook.findByLastName(lastName);
                } else if (choice == 2) {
                    System.out.println("Enter target sum of digits:");
                    int targetSum = scanner.nextInt();
                    phoneBook.findByLotteryNumber(targetSum);
                } else {
                    System.out.println("Invalid option");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
