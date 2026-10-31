
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }
}

class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends BankException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class Account {
    private static int counter = 1001;
    private final String accountNumber;
    private final String holderName;
    protected long balance;

    public Account(String holderName, long balance) {
        this.accountNumber = "AC" + counter++;
        this.holderName = holderName;
        this.balance = balance;
    }

    public synchronized void deposit(long amount)
            throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Amount must be positive.");
        }
        balance += amount;
    }

    public synchronized void withdraw(long amount)
            throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Amount must be positive.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Insufficient balance.");
        }
        balance -= amount;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void transfer(Account receiver, long amount)
            throws BankException {
        if (receiver == null || receiver == this) {
            throw new BankException("Invalid receiver account.");
        }

        Account first = this.accountNumber.compareTo(
            receiver.accountNumber) < 0 ? this : receiver;

        Account second = first == this ? receiver : this;

        synchronized (first) {
            synchronized (second) {
                withdraw(amount);
                receiver.deposit(amount);
            }
        }
    }

    public double interestRate() {
        return 3.0;
    }

    public double yearlyInterest() {
        return balance * interestRate() / 100;
    }

    @Override
    public synchronized String toString() {
        return "Account Number: " + accountNumber
            + ", Holder: " + holderName
            + ", Balance: Rs." + balance;
    }
}

class SavingsAccount extends Account {
    private final long minimumBalance;

    public SavingsAccount(String name, long balance,
                          long minimumBalance) {
        super(name, balance);
        this.minimumBalance = minimumBalance;
    }

    @Override
    public double interestRate() {
        return 5.0;
    }

    @Override
    public synchronized void withdraw(long amount)
            throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Amount must be positive.");
        }
        if (getBalance() - amount < minimumBalance) {
            throw new InsufficientFundsException(
                "Minimum balance must be maintained.");
        }
        super.withdraw(amount);
    }
}

class CurrentAccount extends Account {
    private final long overdraftLimit;

    public CurrentAccount(String name, long balance,
                          long overdraftLimit) {
        super(name, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public synchronized void withdraw(long amount)
            throws BankException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Amount must be positive.");
        }
        if (getBalance() - amount < -overdraftLimit) {
            throw new InsufficientFundsException(
                "Overdraft limit exceeded.");
        }
        super.withdraw(amount);
    }
}

class FixedDepositAccount extends Account {
    public FixedDepositAccount(String name, long balance) {
        super(name, balance);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }
}

@FunctionalInterface
interface WithdrawRule {
    boolean allow(Account account, long amount);
}

public class MiniBank {

    public static void main(String[] args) {

        System.out.println("=== Welcome to MiniBank ===");

        Account[] accounts = {
            new SavingsAccount("Alice", 2000, 500),
            new CurrentAccount("Bob", 1000, 2000),
            new FixedDepositAccount("Charlie", 10000)
        };

        // Exception handling
        System.out.println("\n--- Exception Handling ---");

        try {
            accounts[0].deposit(500);
            System.out.println("Deposit successful.");
        } catch (BankException e) {
            System.out.println(e.getMessage());
        }

        try {
            accounts[1].deposit(-100);
        } catch (BankException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        try {
            accounts[0].withdraw(3000);
        } catch (BankException e) {
            System.out.println("Withdraw error: " + e.getMessage());
        }

        // Transfer
        System.out.println("\n--- Transfer ---");

        try {
            accounts[0].transfer(accounts[1], 500);
            System.out.println("Transfer successful.");
        } catch (BankException e) {
            System.out.println(e.getMessage());
        }

        // Multithreading
        System.out.println("\n--- Multithreading ---");

        Account shared = new SavingsAccount(
            "SharedAccount", 0, 0);

        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    try {
                        shared.deposit(10);
                    } catch (BankException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted.");
                return;
            }
        }

        System.out.println("Expected Balance: Rs.100000");
        System.out.println("Actual Balance: Rs."
            + shared.getBalance());

        // Thread pool
        System.out.println("\n--- Thread Pool ---");

        ExecutorService executor =
            Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            final int id = i;

            executor.submit(() -> {
                try {
                    accounts[0].deposit(100);
                    System.out.println(
                        "Task " + id + " completed.");
                } catch (BankException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        executor.shutdown();

        // Lambda expression
        System.out.println("\n--- Withdraw Rule ---");

        WithdrawRule rule =
            (account, amount) ->
                account.getBalance() >= amount;

        System.out.println("Can withdraw Rs.500: "
            + rule.allow(accounts[0], 500));

        // Polymorphism
        System.out.println("\n--- Account Details ---");

        for (Account account : accounts) {
            System.out.println(account);
            System.out.println(
                "Interest Rate: "
                + account.interestRate() + "%");
            System.out.println(
                "Yearly Interest: Rs."
                + account.yearlyInterest());
        }

        // Interactive menu
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            System.out.println("\n===== MiniBank Menu =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Display Accounts");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Enter a valid number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1 -> {
                        System.out.print("Account (1-3): ");
                        int index = scanner.nextInt() - 1;

                        System.out.print("Amount: ");
                        long amount = scanner.nextLong();

                        if (index < 0 || index >= accounts.length) {
                            System.out.println("Invalid account.");
                            break;
                        }

                        accounts[index].deposit(amount);
                        System.out.println("Deposit successful.");
                    }

                    case 2 -> {
                        System.out.print("Account (1-3): ");
                        int index = scanner.nextInt() - 1;

                        System.out.print("Amount: ");
                        long amount = scanner.nextLong();

                        if (index < 0 || index >= accounts.length) {
                            System.out.println("Invalid account.");
                            break;
                        }

                        accounts[index].withdraw(amount);
                        System.out.println("Withdrawal successful.");
                    }

                    case 3 -> {
                        System.out.print("Sender (1-3): ");
                        int from = scanner.nextInt() - 1;

                        System.out.print("Receiver (1-3): ");
                        int to = scanner.nextInt() - 1;

                        System.out.print("Amount: ");
                        long amount = scanner.nextLong();

                        if (from < 0 || from >= accounts.length
                                || to < 0 || to >= accounts.length) {
                            System.out.println("Invalid account.");
                            break;
                        }

                        accounts[from].transfer(
                            accounts[to], amount);

                        System.out.println("Transfer successful.");
                    }

                    case 4 -> {
                        for (Account account : accounts) {
                            System.out.println(account);
                        }
                    }

                    case 5 -> {
                        running = false;
                        System.out.println("Thank you for using MiniBank!");
                    }

                    default ->
                        System.out.println("Invalid choice.");
                }
            } catch (BankException e) {
                System.out.println("Transaction failed: "
                    + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }

        scanner.close();
    }
}
