package service;

import exception.BankException;
import model.Account;

public class TransferService {
    // Safe transfer using consistent lock ordering by accountNumber
    public static void safeTransfer(Account from, Account to, long amount) throws BankException {
        Account firstLock = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? from : to;
        Account secondLock = from.getAccountNumber().compareTo(to.getAccountNumber()) < 0 ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println("Safe transfer of Rs." + amount + " from " + from.getAccountNumber() + " to " + to.getAccountNumber() + " done.");
            }
        }
    }
}
