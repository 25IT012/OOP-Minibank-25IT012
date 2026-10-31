package service;

import model.Command;

public class TransactionQueue {
    private final Command[] buffer;
    private int count = 0;
    private int head = 0;
    private int tail = 0;

    public TransactionQueue(int capacity) {
        this.buffer = new Command[capacity];
    }

    public synchronized void produce(Command cmd) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }
        buffer[tail] = cmd;
        tail = (tail + 1) % buffer.length;
        count++;
        notifyAll();
    }

    public synchronized Command consume() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        Command cmd = buffer[head];
        head = (head + 1) % buffer.length;
        count--;
        notifyAll();
        return cmd;
    }

    public synchronized int getCount() {
        return count;
    }
}
