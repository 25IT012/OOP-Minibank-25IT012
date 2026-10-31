package util;

public class BankSession implements AutoCloseable {
    private final String sessionUser;

    public BankSession(String sessionUser) {
        this.sessionUser = sessionUser;
        System.out.println("Session started for " + sessionUser);
    }

    public void execute(String action) {
        System.out.println("Session executing: " + action);
    }

    @Override
    public void close() {
        System.out.println("Session cleanly closed for " + sessionUser);
    }
}
