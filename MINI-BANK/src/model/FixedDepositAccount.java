package model;

public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String ownerName, long balance) {
        super(ownerName, balance);
    }

    public FixedDepositAccount(String accountNumber, String ownerName, long balance, boolean active) {
        super(accountNumber, ownerName, balance, active);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false;
    }
}
