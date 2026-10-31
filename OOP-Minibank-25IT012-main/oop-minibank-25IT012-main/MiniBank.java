import exception.*;
import model.*;
import service.AccountWorker;
import service.CommandParser;
import service.TransactionProcessor;
import service.TransferService;
import service.WithdrawRule;
import util.AnnotationValidator;
import util.TransactionBuffer;
import util.TransactionSession;
import util.Validator;
import static util.StatementFormatter.buildStatement;

import java.util.Scanner;

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("MiniBank", "Main Branch");
        System.out.println("=== Welcome to " + info.name() + " (" + info.branch() + ") ===");
        
        Account[] accounts = new Account[3];
        accounts[0] = new SavingsAccount("Alice", 2000, 500);
        accounts[1] = new CurrentAccount("Bob", 1000, 2000);
        accounts[2] = new FixedDepositAccount("Charlie", 10000);

        System.out.println("\n--- Performing Transactions with Exception Handling ---");
        try {
            accounts[0].deposit(500);
            System.out.println("Deposit of 500 into Alice's account successful.");
        } catch (InvalidAmountException e) {
            System.out.println("Deposit error: " + e.getMessage());
        }

        try {
            accounts[1].deposit(-100);
        } catch (InvalidAmountException e) {
            System.out.println("Expected deposit error: " + e.getMessage());
        }

        try {
            accounts[0].withdraw(3000);
        } catch (InsufficientFundsException e) {
            System.out.println("Expected withdraw error: " + e.getMessage() + " (Shortfall: Rs." + e.getShortfall() + ")");
        } catch (InvalidAmountException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } finally {
            System.out.println("Account operations check completed.");
        }

        System.out.println("\n--- Testing Transfer with Exceptions ---");
        try {
            accounts[0].transfer(accounts[1], 500);
        } catch (BankException e) {
            System.out.println("Transfer failed in main: " + e.getMessage());
        }

        System.out.println("\n--- Testing try-with-resources ---");
        try (TransactionSession session = new TransactionSession("TX-9901")) {
            System.out.println("Executing safe transaction inside auto-closable session...");
        }

        System.out.println("\n--- Testing PR9 Multi-Threading & Synchronization ---");
        Account sharedAccount = new SavingsAccount("SharedAccount", 0, 0);
        int threadCount = 10;
        int depositsPerThread = 1000;
        long depositAmount = 10;
        long expectedBalance = (long) threadCount * depositsPerThread * depositAmount;

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(sharedAccount, depositsPerThread, depositAmount), "WorkerThread-" + (i + 1));
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }
        System.out.println("Expected Balance: Rs." + expectedBalance);
        System.out.println("Actual Synchronized Balance: Rs." + sharedAccount.getBalance());
        System.out.println("----------------------------------------------------\n");

        System.out.println("\n--- Testing PR10 ThreadPool, Producer-Consumer & Deadlock Prevention ---");
        // 1. ThreadPool with TransactionProcessor
        TransactionProcessor processor = new TransactionProcessor();
        for (int i = 0; i < 5; i++) {
            final int taskId = i + 1;
            processor.submit(() -> {
                try {
                    accounts[0].deposit(100);
                    System.out.println("Pool task " + taskId + " deposited 100 to Alice's account.");
                } catch (InvalidAmountException e) {
                    System.out.println("Pool error: " + e.getMessage());
                }
            });
        }
        processor.stop();

        // 2. Producer-Consumer buffer using wait/notify
        TransactionBuffer buffer = new TransactionBuffer();
        Thread producerThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                buffer.produce(new Command(TransactionType.DEPOSIT, "AC0001", i * 100));
            }
        }, "ProducerThread");

        Thread consumerThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                buffer.consume();
            }
        }, "ConsumerThread");

        producerThread.start();
        consumerThread.start();
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
        }

        // 3. Deadlock prevention with consistent lock ordering
        Thread t1 = new Thread(() -> {
            try {
                TransferService.safeTransfer(accounts[0], accounts[1], 100);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        }, "Transfer-A-to-B");

        Thread t2 = new Thread(() -> {
            try {
                TransferService.safeTransfer(accounts[1], accounts[0], 50);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        }, "Transfer-B-to-A");

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e.getMessage());
        }
        System.out.println("----------------------------------------------------\n");

        System.out.println("\n--- Printing Accounts (Testing toString) ---");
        for (Account acc : accounts) {
            System.out.println(acc.toString());
        }

        System.out.println("\n--- Testing Polymorphism and Default Interface Method ---");
        for (Account acc : accounts) {
            System.out.println("Account: " + acc.getAccountNumber() + 
                               " | Rate: " + acc.interestRate() + "%" +
                               " | Yearly Interest: Rs." + acc.yearlyInterest());
            
            if (acc instanceof Premium) {
                System.out.println("  -> " + acc.getAccountNumber() + " is marked as a Premium account.");
            }
        }
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing WithdrawRule (Functional Interface) ---");
        WithdrawRule dailyLimitRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return amount <= 5000;
            }
        };

        WithdrawRule positiveBalanceRule = (account, amount) -> account.getBalance() >= amount;

        System.out.println("Daily limit check (Withdraw 6000): " + dailyLimitRule.allow(accounts[0], 6000));
        System.out.println("Positive balance check (Withdraw 500): " + positiveBalanceRule.allow(accounts[0], 500));
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing PR7 Custom Annotations & Reflection Validator ---");
        Account badAccount = new SavingsAccount("Dave", -500, 500);
        String[] errors = AnnotationValidator.validate(badAccount);
        System.out.println("Validation errors for bad account (opening balance -500):");
        for (String err : errors) {
            System.out.println(" - " + err);
        }
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing PR4 Features ---");
        System.out.println("Valid Mobile (9876543210): " + Validator.isValidMobile("9876543210"));
        System.out.println("Valid Email (test@example.com): " + Validator.isValidEmail("test@example.com"));

        System.out.println("\nTesting Command Parser:");
        String sampleCommand = "DEPOSIT AC0001 500";
        Command parsedCommand = CommandParser.parse(sampleCommand);
        if (parsedCommand != null) {
            System.out.println("Parsed Command -> Type: " + parsedCommand.type() + 
                               ", Account: " + parsedCommand.accountNumber() + 
                               ", Amount: " + parsedCommand.amount());
        }

        System.out.println("\nTesting Statement Formatter (Static Import):");
        System.out.println(buildStatement(accounts[0]));
        System.out.println("------------------------------\n");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\nMenu Options:");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                scanner.next(); 
                continue;
            }
            
            int choice = scanner.nextInt();
            
            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> null;
            };
            
            if (option == null) {
                System.out.println("Invalid choice. Please select a valid option.");
                continue;
            }
            
            String message = switch (option) {
                case OPEN_ACCOUNT -> "Open Account — to be implemented in a later lab";
                case DEPOSIT -> "Deposit — to be implemented in a later lab";
                case WITHDRAW -> "Withdraw — to be implemented in a later lab";
                case TRANSFER -> "Transfer — to be implemented in a later lab";
                case EXIT -> "Goodbye!";
            };
            
            System.out.println(message);
            
            if (option == MenuOption.EXIT) {
                running = false;
            }
        }
        
        scanner.close();
    }
}
