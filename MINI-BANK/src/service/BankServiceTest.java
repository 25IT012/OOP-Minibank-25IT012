package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import model.SavingsAccount;
import org.junit.jupiter.api.Test;

public class BankServiceTest {

    @Test
    public void testDepositIncreasesBalance() throws BankException {
        BankService service = new BankService();
        Account acc = service.openAccount("SAVINGS", "John Doe", 5000, "9876543210", "john@example.com");
        service.deposit(acc.getAccountNumber(), 2500);
        assertEquals(7500, acc.getBalance());
    }

    @Test
    public void testWithdrawalOverLimitThrowsException() throws BankException {
        BankService service = new BankService();
        Account acc = service.openAccount("SAVINGS", "Jane Doe", 5000, "9876543210", "jane@example.com");

        assertThrows(InsufficientFundsException.class, () -> {
            service.withdraw(acc.getAccountNumber(), 8000);
        });
    }

    @Test
    public void testConcurrentDepositsKeepBalanceCorrect() throws Exception {
        Account account = new SavingsAccount("Concurrent User", 1000, 500);
        int threadCount = 10;
        int depositsPerThread = 100;
        long amountPerDeposit = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(account, depositsPerThread, amountPerDeposit));
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            threads[i].join();
        }

        long expected = 1000 + (threadCount * depositsPerThread * amountPerDeposit);
        assertEquals(expected, account.getBalance());
    }

    public static void main(String[] args) {
        BankServiceTest runner = new BankServiceTest();
        int passed = 0;
        int total = 3;

        try {
            runner.testDepositIncreasesBalance();
            System.out.println("[PASS] testDepositIncreasesBalance");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] testDepositIncreasesBalance: " + e.getMessage());
        }

        try {
            runner.testWithdrawalOverLimitThrowsException();
            System.out.println("[PASS] testWithdrawalOverLimitThrowsException");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] testWithdrawalOverLimitThrowsException: " + e.getMessage());
        }

        try {
            runner.testConcurrentDepositsKeepBalanceCorrect();
            System.out.println("[PASS] testConcurrentDepositsKeepBalanceCorrect");
            passed++;
        } catch (Throwable e) {
            System.out.println("[FAIL] testConcurrentDepositsKeepBalanceCorrect: " + e.getMessage());
        }

        System.out.println("Tests executed: " + total + ", Passed: " + passed + ", Failed: " + (total - passed));
    }
}
