package service;

import static java.lang.System.out;
import java.util.ArrayList;
import model.Account;
import model.Customer;
import model.SavingsAccount;
import util.Repository;

public class Driver {
    public static void main(String[] args) {
        Repository<Account> accountRepo = new Repository<>();

        Account a1 = new SavingsAccount("Ananya", 12000, 1000);
        Account a2 = new SavingsAccount("Manish", 18000, 1000);
        Account a3 = new SavingsAccount("Kavita", 25000, 1000);

        accountRepo.saveAll(a1, a2, a3);

        out.println("Stored accounts count: " + accountRepo.size());
        out.println("Finding account by id " + a2.getAccountNumber() + ":");
        Account found = accountRepo.findById(a2.getAccountNumber());
        out.println("Found Account: " + found);

        out.println("\nAll accounts in Repository<Account>:");
        ArrayList<Account> allAccounts = accountRepo.findAll();
        for (Account a : allAccounts) {
            out.println(a);
        }

        out.println("\nReusing same Repository class for Customer entities:");
        Repository<Customer> customerRepo = new Repository<>();
        Customer c1 = new Customer("Ananya", "ananya@email.com", "9876501234");
        Customer c2 = new Customer("Manish", "manish@email.com", "9876505678");

        customerRepo.save(c1);
        customerRepo.save(c2);

        out.println("Stored customers count: " + customerRepo.size());
        out.println("Finding customer by id " + c1.getCustomerId() + ":");
        Customer foundCustomer = customerRepo.findById(c1.getCustomerId());
        out.println("Found Customer: " + foundCustomer);

        Double[] balances = new Double[]{12000.0, 18000.0, 25000.0};
        double avg = Repository.average(balances);
        out.println("\nBounded generic average of balances: " + avg);
    }
}
