package service;

import static java.lang.System.out;
import exception.BankException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.Account;
import model.Command;
import model.SavingsAccount;
import model.TransactionType;
import util.ReportGenerator;
import util.TransactionLog;

record BankInfo(String name, String branch) {}

public class MiniBank {
    public static void main(String[] args) {
        BankInfo bankInfo = new BankInfo("MiniBank Enterprise", "Main Branch, Ahmedabad");
        out.println("==========================================");
        out.println(bankInfo.name() + " - " + bankInfo.branch());
        out.println("==========================================");

        BankService bankService = new BankService();
        Path dataDir = Paths.get("data");
        Path logsDir = Paths.get("logs");
        Path accountsFile = dataDir.resolve("accounts.dat");
        Path logFile = logsDir.resolve("transactions.log");
        Path reportFile = dataDir.resolve("eod_summary.txt");

        if (Files.exists(accountsFile)) {
            bankService.loadAccounts(accountsFile);
            out.println("Loaded existing accounts from " + accountsFile);
        } else {
            try {
                Account defaultAcc1 = new SavingsAccount("Aryan Amdavadi", 25000, 1000);
                Account defaultAcc2 = new SavingsAccount("Rohan Sharma", 15000, 1000);
                bankService.getRepository().save(defaultAcc1);
                bankService.getRepository().save(defaultAcc2);
            } catch (Exception ignored) {
            }
        }

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 9) {
            out.println("\n===== MiniBank Menu =====");
            out.println("1. Open Account");
            out.println("2. Deposit Money");
            out.println("3. Withdraw Money");
            out.println("4. Transfer Money");
            out.println("5. View Account Statement");
            out.println("6. List All Accounts");
            out.println("7. Run Batch Transactions (ThreadPool)");
            out.println("8. Generate End-of-Day Report");
            out.println("9. Exit Application");
            out.print("Enter your choice (1-9): ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                out.println("Invalid input. Please enter a valid number from 1 to 9.");
                continue;
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    out.println("\n--- Open Account ---");
                    out.print("Select Account Type (1. Savings, 2. Current, 3. Fixed Deposit): ");
                    String type = scanner.nextLine().trim();
                    out.print("Enter Owner Full Name: ");
                    String name = scanner.nextLine().trim();
                    out.print("Enter Opening Balance: ");
                    long balance = 0;
                    try {
                        balance = Long.parseLong(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        out.println("Invalid balance format.");
                        break;
                    }
                    out.print("Enter 10-digit Mobile Number: ");
                    String mobile = scanner.nextLine().trim();
                    out.print("Enter Valid Email Address: ");
                    String email = scanner.nextLine().trim();

                    try {
                        Account created = bankService.openAccount(type, name, balance, mobile, email);
                        out.println("Account created successfully!");
                        out.println("Account Number: " + created.getAccountNumber());
                        out.println("Account Holder: " + created.getOwnerName());
                        out.println("Balance: Rs. " + created.getBalance());
                    } catch (BankException e) {
                        out.println("Failed to open account: " + e.getMessage());
                    }
                }
                case 2 -> {
                    out.println("\n--- Deposit ---");
                    out.print("Enter Account Number: ");
                    String accNum = scanner.nextLine().trim();
                    out.print("Enter Amount to Deposit: ");
                    try {
                        long amount = Long.parseLong(scanner.nextLine().trim());
                        bankService.deposit(accNum, amount);
                        TransactionLog.append(logFile, "DEPOSIT " + accNum + " " + amount);
                        out.println("Deposit successful! New balance: Rs. " + bankService.getRepository().findById(accNum).getBalance());
                    } catch (NumberFormatException e) {
                        out.println("Invalid amount format.");
                    } catch (BankException | IOException e) {
                        out.println("Deposit error: " + e.getMessage());
                    }
                }
                case 3 -> {
                    out.println("\n--- Withdraw ---");
                    out.print("Enter Account Number: ");
                    String accNum = scanner.nextLine().trim();
                    out.print("Enter Amount to Withdraw: ");
                    try {
                        long amount = Long.parseLong(scanner.nextLine().trim());
                        bankService.withdraw(accNum, amount);
                        TransactionLog.append(logFile, "WITHDRAW " + accNum + " " + amount);
                        out.println("Withdrawal successful! New balance: Rs. " + bankService.getRepository().findById(accNum).getBalance());
                    } catch (NumberFormatException e) {
                        out.println("Invalid amount format.");
                    } catch (BankException | IOException e) {
                        out.println("Withdrawal error: " + e.getMessage());
                    }
                }
                case 4 -> {
                    out.println("\n--- Transfer ---");
                    out.print("Enter Source Account Number: ");
                    String fromAcc = scanner.nextLine().trim();
                    out.print("Enter Destination Account Number: ");
                    String toAcc = scanner.nextLine().trim();
                    out.print("Enter Amount to Transfer: ");
                    try {
                        long amount = Long.parseLong(scanner.nextLine().trim());
                        bankService.transfer(fromAcc, toAcc, amount);
                        TransactionLog.append(logFile, "WITHDRAW " + fromAcc + " " + amount);
                        TransactionLog.append(logFile, "DEPOSIT " + toAcc + " " + amount);
                        out.println("Transfer successful of Rs. " + amount + " from " + fromAcc + " to " + toAcc);
                    } catch (NumberFormatException e) {
                        out.println("Invalid amount format.");
                    } catch (BankException | IOException e) {
                        out.println("Transfer error: " + e.getMessage());
                    }
                }
                case 5 -> {
                    out.println("\n--- Statement ---");
                    out.print("Enter Account Number: ");
                    String accNum = scanner.nextLine().trim();
                    try {
                        String st = bankService.statement(accNum);
                        out.println(st);
                    } catch (BankException e) {
                        out.println("Error generating statement: " + e.getMessage());
                    }
                }
                case 6 -> {
                    out.println("\n--- All Accounts in Bank ---");
                    ArrayList<Account> accounts = bankService.listAccounts();
                    if (accounts.isEmpty()) {
                        out.println("No accounts found in bank repository.");
                    } else {
                        for (Account a : accounts) {
                            out.println(a.getAccountNumber() + " | " + a.getOwnerName() + " | Rs. " + a.getBalance() + " (" + a.getClass().getSimpleName() + ")");
                        }
                    }
                }
                case 7 -> {
                    out.println("\n--- Safe Batch Processing ---");
                    ArrayList<Account> currentList = bankService.listAccounts();
                    if (currentList.isEmpty()) {
                        out.println("No accounts available for batch processing.");
                        break;
                    }
                    String targetAcc = currentList.get(0).getAccountNumber();
                    List<Command> batch = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        batch.add(new Command(TransactionType.DEPOSIT, targetAcc, 100));
                    }
                    out.println("Executing batch of 10 concurrent deposits on " + targetAcc + " via TransactionProcessor...");
                    TransactionProcessor processor = new TransactionProcessor(4);
                    bankService.processBatch(processor, batch, logFile);
                    out.println("Batch execution complete! Current balance of " + targetAcc + ": Rs. " + bankService.getRepository().findById(targetAcc).getBalance());
                }
                case 8 -> {
                    out.println("\n--- End-of-Day Summary Report ---");
                    try {
                        ReportGenerator.generateReport(logsDir, reportFile);
                        if (Files.exists(reportFile)) {
                            out.println(Files.readString(reportFile));
                        }
                    } catch (IOException e) {
                        out.println("Report generation error: " + e.getMessage());
                    }
                }
                case 9 -> {
                    out.println("\nSaving application state before exit...");
                    try {
                        bankService.saveAccounts(accountsFile);
                        out.println("All accounts successfully persisted to " + accountsFile);
                    } catch (IOException e) {
                        out.println("Error saving accounts: " + e.getMessage());
                    }
                    out.println("Thank you for using MiniBank! Goodbye.");
                }
                default -> out.println("Invalid choice. Please choose from options 1 to 9.");
            }
        }
        scanner.close();
    }
}
