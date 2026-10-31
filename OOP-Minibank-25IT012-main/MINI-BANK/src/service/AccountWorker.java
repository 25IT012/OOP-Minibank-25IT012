package service;

import exception.InvalidAmountException;
import model.Account;

public class AccountWorker implements Runnable {
    private final Account account;
    private final int times;
    private final long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " running for account " + account.getAccountNumber());
        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (InvalidAmountException e) {
                System.out.println("Deposit error: " + e.getMessage());
            }
        }
        System.out.println(Thread.currentThread().getName() + " completed.");
    }
}
