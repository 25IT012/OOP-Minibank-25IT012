package util;

import model.Account;

public class StatementFormatter {
    public static String buildStatement(Account account) {
        if (account == null) {
            return "Account is null";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("          ACCOUNT STATEMENT         \n");
        sb.append("====================================\n");
        sb.append("Account Number : ").append(account.getAccountNumber()).append("\n");
        sb.append("Owner Name     : ").append(account.getOwnerName()).append("\n");
        sb.append("Current Balance: Rs.").append(account.getBalance()).append("\n");
        sb.append("Status         : ").append(account.isActive() ? "Active" : "Inactive").append("\n");
        sb.append("====================================\n");
        
        return sb.toString();
    }
}
