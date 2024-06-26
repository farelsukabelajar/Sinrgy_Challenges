package synrgy.binfod.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import synrgy.binfod.model.*;
import synrgy.binfod.util.ErrorHandling;
import synrgy.binfod.util.ReceiptUpdater;
import synrgy.binfod.view.MainView;

public class MainController {
    private Menu menu;
    private MainView view;
    private Scanner scanner;

    public MainController(Menu menu, MainView view) {
        this.menu = menu;
        this.view = view;
        this.scanner = new Scanner(System.in);
    }

    public void chooseMenu(List<Order> orders) {
        boolean addMore = true;
        do {
            List<MenuItem> menuItems = menu.getMenuItems();
            view.displayItems(menuItems);
            System.out.print("\nPilih Menu: ");
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= menuItems.size()) {
                MenuItem menuItem = menuItems.get(choice - 1);
                System.out.print("Jumlah Pesanan: ");
                int qty;
                qty = scanner.nextInt();
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
        if (!orders.isEmpty()) {
            confirmOrder(orders);
        } else {
            System.out.println("\nTidak ada pesanan untuk dikonfirmasi.");
        }
    }

    public void run() {
        int choice;
        List<Order> orders = new ArrayList<>();

        do {
            view.displayMenu();
            try {
                System.out.print("Pilihan Anda: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        chooseMenu(orders);
                        break;
                    case 2:
                        if (!orders.isEmpty()) {
                            view.displayReceipt(orders);
                        } else {
                            System.out.println("\nTidak ada pesanan untuk ditampilkan.");
                        }
                        break;
                    case 0:
                        System.out.println("\nTerima kasih telah menggunakan Binar Food.\n");
                        break;
                    default:
                        System.out.println("\nPilihan tidak valid. Silakan pilih lagi.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("\nMasukkan nomor menu yang valid.");
                scanner.nextLine();
                choice = -1;
            }
        } while (choice != 0);
    }

    public void confirmOrder(List<Order> orders) {
        view.displayConfirm();
        System.out.print("\nApakah Anda ingin mengkonfirmasi pesanan? (y/t): ");
        String confirmation = scanner.next();
        if (confirmation.equalsIgnoreCase("y")) {
            saveReceiptToFile(orders);
            System.out.println("\nPesanan telah dikonfirmasi.");
        } else {
            System.out.println("\nPesanan dibatalkan.");
            orders.clear();
        }
    }

    public Order createOrder(MenuItem menuItem, int quantity) {
        if (menuItem instanceof FoodMenuItem) {
            return new FoodOrder(menuItem, quantity);
        } else if (menuItem instanceof DrinkMenuItem) {
            return new DrinkOrder(menuItem, quantity);
        } else {
            throw new IllegalArgumentException("MenuItem tidak valid.");
        }
    }

    private void saveReceiptToFile(List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("struk.txt", true))) {
            String receipt = ReceiptUpdater.generateReceipt(orders);
            writer.write(receipt);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            ErrorHandling.handleException(e);
        }
    }
    
}
