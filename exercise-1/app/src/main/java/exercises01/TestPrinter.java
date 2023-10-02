package exercises01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestPrinter {
    Printer printer = new Printer();

    public TestPrinter() {
        Thread t1 = new Thread(() -> {
            while(true)
            {
                printer.print();
            }
        });
        Thread t2 = new Thread(() -> {
            while(true)
            {
                printer.print();
            }
        });

        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
    }

    public static void main(String[] args) {
		new TestPrinter();
    }

    class Printer {
        private Lock lock = new ReentrantLock();

        public void print() {
            lock.lock();

            try {
                System.out.print("-");
                try { Thread.sleep(50); } catch (InterruptedException exn) { }
                System.out.print("|");
            } finally {
                lock.unlock();
            }
        }
    }
}
