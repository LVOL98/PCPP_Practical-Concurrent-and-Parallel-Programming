package exercises07;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {
    private final AtomicInteger[] counts;

    public CasHistogram(int span) {
        counts = new AtomicInteger[span];

        for (int i = 0; i < span; i++) {
            counts[i] = new AtomicInteger(0);
        }
    }

    @Override
    public void increment(int bin) {
        int oldValue;
        var counter = counts[bin];

        do {
            oldValue = counter.get();
        } while(!counter.compareAndSet(oldValue, oldValue + 1));
    }

    @Override
    public int getCount(int bin) {
        return counts[bin].get();
    }

    @Override
    public int getSpan() {
        return counts.length;
    }

    @Override
    public int getAndClear(int bin) {
        int oldValue;
        var counter = counts[bin];

        do {
            oldValue = counter.get();
        } while (!counter.compareAndSet(oldValue, 0));

        return oldValue;
    }
    
}
