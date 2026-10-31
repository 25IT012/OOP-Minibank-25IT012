package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.Account;

public class TransactionProcessor {
    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();

    public TransactionProcessor() {
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public TransactionProcessor(int poolSize) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public void registerAccount(Account account) {
        if (account != null && account.getAccountNumber() != null) {
            sharedAccounts.put(account.getAccountNumber(), account);
        }
    }

    public Account getSharedAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }

    public void submit(Runnable task) {
        executorService.execute(task);
    }

    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
