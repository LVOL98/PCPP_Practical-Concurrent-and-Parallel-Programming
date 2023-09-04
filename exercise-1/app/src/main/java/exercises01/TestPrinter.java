package exercises01;

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
        public synchronized void print() {
            System.out.print("-");
            try {
                Thread.sleep(50);
            } catch (InterruptedException exn) {
            }
            System.out.print("|");
        }
    }
}
