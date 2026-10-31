package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Account;

public class AccountStore {
    private final Map<String, Account> accounts = new HashMap<>();
    private final List<String> transactionHistory = new ArrayList<>();

    public void add(Account account) {
        if (account != null && account.getAccountNumber() != null) {
            accounts.put(account.getAccountNumber(), account);
            transactionHistory.add("Added account: " + account.getAccountNumber() + " (" + account.getOwnerName() + ")");
        }
    }

    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void recordTransaction(String entry) {
        transactionHistory.add(entry);
    }

    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }
}
