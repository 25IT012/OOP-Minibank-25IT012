package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.annotation.Audit;
import util.AuditLogger;
import util.Repository;
import util.StatePersister;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;

public class BankService {
    private final Repository<Account> repository;

    public BankService() {
        this.repository = new Repository<>();
    }

    public BankService(Repository<Account> repository) {
        this.repository = repository;
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(String type, String ownerName, long initialBalance, String mobile, String email) throws BankException {
        if (!Validator.isValidMobile(mobile)) {
            throw new BankException("Invalid mobile number format: " + mobile);
        }
        if (!Validator.isValidEmail(email)) {
            throw new BankException("Invalid email address format: " + email);
        }
        if (initialBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative: " + initialBalance);
        }

        Account account;
        String normalizedType = type.trim().toUpperCase();
        if (normalizedType.equals("SAVINGS") || normalizedType.equals("1")) {
            account = new SavingsAccount(ownerName, initialBalance, 1000);
        } else if (normalizedType.equals("CURRENT") || normalizedType.equals("2")) {
            account = new CurrentAccount(ownerName, initialBalance, 5000);
        } else if (normalizedType.equals("FD") || normalizedType.equals("FIXED_DEPOSIT") || normalizedType.equals("3")) {
            account = new FixedDepositAccount(ownerName, initialBalance);
        } else {
            throw new BankException("Unsupported account type: " + type);
        }

        repository.save(account);
        AuditLogger.log(BankService.class, "openAccount");
        return account;
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found");
        }
        account.deposit(amount);
        AuditLogger.log(BankService.class, "deposit");
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found");
        }
        account.withdraw(amount);
        AuditLogger.log(BankService.class, "withdraw");
    }

    @Audit(action = "TRANSFER")
    public void transfer(String fromAccountNumber, String toAccountNumber, long amount) throws BankException {
        Account from = repository.findById(fromAccountNumber);
        if (from == null) {
            throw new AccountNotFoundException("Sender account " + fromAccountNumber + " not found");
        }
        Account to = repository.findById(toAccountNumber);
        if (to == null) {
            throw new AccountNotFoundException("Receiver account " + toAccountNumber + " not found");
        }
        from.transfer(to, amount);
        AuditLogger.log(BankService.class, "transfer");
    }

    @Audit(action = "STATEMENT")
    public String statement(String accountNumber) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account " + accountNumber + " not found");
        }
        AuditLogger.log(BankService.class, "statement");
        return StatementFormatter.buildStatement(account);
    }

    public ArrayList<Account> listAccounts() {
        return repository.findAll();
    }

    public void saveAccounts(Path file) throws IOException {
        ArrayList<Account> accounts = repository.findAll();
        StatePersister.save(accounts.toArray(new Account[0]), file);
    }

    public void loadAccounts(Path file) {
        try {
            Account[] accounts = StatePersister.load(file);
            if (accounts != null) {
                for (Account acc : accounts) {
                    repository.save(acc);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void processBatch(TransactionProcessor processor, List<Command> batch, Path logFile) {
        for (Account acc : repository.findAll()) {
            processor.registerAccount(acc);
        }

        for (Command cmd : batch) {
            processor.submit(() -> {
                try {
                    Account acc = processor.getSharedAccount(cmd.accountNumber());
                    if (acc != null) {
                        if (cmd.type() == model.TransactionType.DEPOSIT) {
                            acc.deposit(cmd.amount());
                            if (logFile != null) {
                                TransactionLog.append(logFile, "DEPOSIT " + cmd.accountNumber() + " " + cmd.amount());
                            }
                        } else if (cmd.type() == model.TransactionType.WITHDRAW) {
                            acc.withdraw(cmd.amount());
                            if (logFile != null) {
                                TransactionLog.append(logFile, "WITHDRAW " + cmd.accountNumber() + " " + cmd.amount());
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        }
        processor.stop();
    }
}
