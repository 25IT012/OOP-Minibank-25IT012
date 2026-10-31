package util;

import model.Command;

public class TransactionBuffer {
    private Command[] buffer = new Command[5];
    private int count = 0;

    public synchronized void produce(Command command) {
        while (count == buffer.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        buffer[count] = command;
        count++;
        System.out.println("Produced: " + command.type() + " for " + command.accountNumber());
        notifyAll();
    }

    public synchronized Command consume() {
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        count--;
        Command command = buffer[count];
        System.out.println("Consumed: " + command.type() + " for " + command.accountNumber());
        notifyAll();
        return command;
    }
}
