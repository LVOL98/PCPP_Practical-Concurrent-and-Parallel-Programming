package exercises05;

import benchmarking.Benchmark;

public class TimingVolatile {
    public static void main(String[] args) {
        var volatileInt = new VolatileInt();
        var repetition = 5;

        for (var i = 0; i < repetition; i++) {
            Benchmark.Mark7("Test of non-volatile int", k -> {
                volatileInt.inc();
                return volatileInt.hashCode();
            });
        }
        for (var i = 0; i < repetition; i++) {
            Benchmark.Mark7("Test of volatile int", k -> {
                volatileInt.vInc();
                return volatileInt.hashCode();
            });
        }
    }
}

class VolatileInt {
    private volatile int vCtr;
    private int ctr;

    public void vInc() {
        vCtr++;
    }

    public void inc() {
        ctr++;
    }
}