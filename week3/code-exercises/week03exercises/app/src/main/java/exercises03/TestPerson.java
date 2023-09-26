package exercises03;

import java.util.LinkedList;
import java.util.List;

class TestPerson {
    private static volatile boolean t2ShouldRun = true;
    private static volatile List<Person> persons = new LinkedList<Person>();

    public static void main(String[] args) {        
        int iterations = 100000;

        Thread t1 = new Thread(() -> {
            int zip = 4300;
            var address = "Thread 1";

            for (var i = 0; i < iterations; i++) {
                var person = new Person();
                persons.add(i, person);

                var result = person.setZipAndAddress(zip, address);

                // if (result.zip != zip || result.address != address) {
                //     System.out.println(String.format("Iteration: %d, expected: %s, got %s", i, zip + " " + address, result));
                // }
            }

            t2ShouldRun = false;
        });
        Thread t2 = new Thread(() -> {
            var zip = 2000;
            var address = "Thread 2";

            while(t2ShouldRun) {
                int previousId = (int) (Person.getPreviousId());

                if (previousId != -1 && persons.size() == previousId + 1) {
                    Person person = persons.get(previousId);

                    person.setZipAndAddress(zip, address);
                }
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
        }
    }
}

class Person {
    private static long previousId = -1;
    private final long id;
    private String name;
    private int zip;
    private String address;

    public long getId() {
        return id;
    }

    public static long getPreviousId() {
        return previousId;
    }

    public String getName() {
        return name;
    }

    public int getZip() {
        return zip;
    }

    public String getAddress() {
        return address;
    }

    public synchronized MyTuple2 setZipAndAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;

        return new MyTuple2<Integer,String>(this.zip, this.address);
    }

    public Person() {
        this.id = incrementAndGetPreviousId();
    }

    public Person(long id) {
        this.id = incrementAndGetPreviousId(id);
    }

    private static synchronized long incrementAndGetPreviousId() {
        return ++previousId;
    }

    private static synchronized long incrementAndGetPreviousId(long id) {
        if (previousId == -1) {
            previousId = id - 1;
        }

        return ++previousId;
    }
}

class TempTestZipAddress {
    public int zip;
    public String address;

    public TempTestZipAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }

    @Override
    public String toString() {
        return "zip: " + zip + ", address: " + address;
    }
}
