package Chalenges.Ch_2.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Chalenges.Ch_2.model.DrinkMenuItem;
import Chalenges.Ch_2.model.DrinkOrder;
import Chalenges.Ch_2.model.FoodMenuItem;
import Chalenges.Ch_2.model.FoodOrder;
import Chalenges.Ch_2.model.Menu;
import Chalenges.Ch_2.model.MenuItem;
import Chalenges.Ch_2.model.Order;
import Chalenges.Ch_2.util.ReceiptUpdater;
import Chalenges.Ch_2.view.MainView;

public class OrderService {
    private List<Order> orders;

    public OrderService() {
        this.orders = new ArrayList<>();
    }

    public List<Order> createOrderProcess(Menu menu, MainView view, Scanner scanner) {
        boolean addMore = true;
        do {
            List<MenuItem> menuItems = menu.getMenuItems();
            view.displayItems(menuItems);
            System.out.print("\nPilih Menu: ");
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= menuItems.size()) {
                MenuItem menuItem = menuItems.get(choice - 1);
                System.out.print("Jumlah Pesanan: ");
                int qty = scanner.nextInt();
                if (qty >= 0) {
                    Order order = createOrder(menuItem, qty);
                    orders.add(order);
                    view.displayAddMenu();
                    System.out.print("\nApakah Anda ingin menambah pesanan lagi? (y/t): ");
                    addMore = scanner.next().equalsIgnoreCase("y");
                } else {
                    System.out.println("\nJumlah pesanan harus non-negatif.");
                }
            } else {
                System.out.println("\nPilihan tidak valid. Silakan pilih lagi.");
            }
        } while (addMore);
        return orders;
    }

    private Order createOrder(MenuItem menuItem, int quantity) {
        if (menuItem instanceof FoodMenuItem) {
            return new FoodOrder((FoodMenuItem) menuItem, quantity);
        } else if (menuItem instanceof DrinkMenuItem) {
            return new DrinkOrder((DrinkMenuItem) menuItem, quantity);
        } else {
            throw new IllegalArgumentException("MenuItem tidak valid.");
        }
    }

    public void saveReceiptToFile(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("struk.txt", true))) {
            String receipt = ReceiptUpdater.generateReceipt(orders);
            writer.write(receipt);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getOrders() {
        return orders;
    }
}
