// For week 7
// raup@itu.dk * 10/10/2021

package exercises07;

import java.util.concurrent.atomic.AtomicReference;

// Very likely you will need some imports here

class ReadWriteCASLock implements SimpleRWTryLockInterface {
    private final AtomicReference<Holders> holdersReference = new AtomicReference<Holders>();

    public boolean readerTryLock() {
        var holders = holdersReference.get();
        ReaderList readers;
        ReaderList readersNew;

        do {
            if (holders instanceof Writer) {
                return false;
            }

            readers = (ReaderList) holders;
            readersNew = new ReaderList(Thread.currentThread(), readers);
        } while (!holdersReference.compareAndSet(holders, readersNew));

        return true;
    }

    public void readerUnlock() {
        var currentThread = Thread.currentThread();
        Holders holders;
        ReaderList readers;
        ReaderList readersNew;

        do {
            holders =  holdersReference.get();

            if (holders == null) {
                throw new IllegalStateException("No locks to release");
            }
    
            if (holders instanceof Writer) {
                throw new IllegalStateException("Cannot release a write lock");
            }

            readers = (ReaderList) holders;
            if (!readers.contains(currentThread)) {
                throw new IllegalStateException("Thread holds no reader lock");
            }

            readersNew = readers.remove(currentThread);
        } while (!holdersReference.compareAndSet(holders, readersNew));
    }

    // TODO: fairness? starvation?
    public boolean writerTryLock() {
        var holders = holdersReference.get();

        if (holders != null) {
            return false;
        }

        return holdersReference.compareAndSet(holders, new Writer(Thread.currentThread()));
    }

    public void writerUnlock() {
        var holders = holdersReference.get();

        if (holders == null) {
            throw new IllegalStateException("No one holds the lock");
        } else if (((Writer)holders).thread != Thread.currentThread()) {
            throw new IllegalStateException("Cannot unlock a different thread than itself");
        } else if (holders instanceof ReaderList) {
            throw new IllegalStateException("Cannot unlock a reader lock with write unlock");
        } 

        // TODO: need a do while loop?
        holdersReference.compareAndSet(holders, null);
    }

    // Challenging 7.2.7: You may add new methods

    private static abstract class Holders { }

    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;

        public ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }

        public boolean contains(Thread t) {
            var reader = this;

            do {
                if (reader.thread == t) {
                    return true;
                }

                reader = reader.next;
            } while (reader != null);

            return false;
        }

        public ReaderList remove(Thread t) {
            if (this.next == null) {
                if (this.thread == t) {
                    return null;
                } else {
                    return new ReaderList(this.thread, null);
                }
            } else {
                if (this.thread == t) {
                    return this.next.remove(t);
                } else {
                    return new ReaderList(this.thread, this.next.remove(t));
                }
            }
        }
    }

    private static class Writer extends Holders {
        public final Thread thread;

        public Writer(Thread thread) {
            this.thread = thread;
        }
    }
}
