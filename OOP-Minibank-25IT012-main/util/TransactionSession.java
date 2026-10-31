package util;

public class TransactionSession implements AutoCloseable {
    private String sessionId;

    public TransactionSession(String sessionId) {
        this.sessionId = sessionId;
        System.out.println("Transaction session " + sessionId + " opened.");
    }

    @Override
    public void close() {
        System.out.println("Transaction session " + sessionId + " closed.");
    }
}
