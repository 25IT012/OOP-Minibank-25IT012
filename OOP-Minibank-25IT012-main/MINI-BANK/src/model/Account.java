package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.io.Serializable;
import model.annotation.Id;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;
    private String ownerName;
    @Positive
    private long balance;
    private boolean active;
    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return "AC" + String.format("%04d", accountCounter);
    }

    public Account(String ownerName, long balance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public Account(String accountNumber, String ownerName, long balance, boolean active) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = active;
    }

    @Override
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive: " + amount);
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive: " + amount);
        }
        if (!canWithdraw(amount)) {
            long shortfall = amount - balance;
            if (shortfall < 0) {
                shortfall = 0;
            }
            throw new InsufficientFundsException("Insufficient funds for withdrawal. Balance: " + balance + ", Requested: " + amount, shortfall);
        }
        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Destination account not found");
        }
        Account first = this.accountNumber.compareTo(to.accountNumber) < 0 ? this : to;
        Account second = first == this ? to : this;

        synchronized (first) {
            synchronized (second) {
                this.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int compareTo(Account other) {
        if (other == null || other.accountNumber == null) {
            return 1;
        }
        if (this.accountNumber == null) {
            return -1;
        }
        return this.accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[accountNumber=" + accountNumber + ", ownerName=" + ownerName + ", balance=" + balance + ", active=" + active + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber != null && accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber != null ? accountNumber.hashCode() : 0;
    }
}
