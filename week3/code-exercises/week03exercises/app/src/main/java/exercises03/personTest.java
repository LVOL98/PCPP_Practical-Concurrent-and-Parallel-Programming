package exercises03;

public class personTest {
    
}

class Person {
    private static long previousId = -1;
    private final long id;
    private String name;
    private int zip;
    private String address;

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int zip() {
        return zip;
    }

    public String address() {
        return address;
    }

    public Person() {
        if (previousId != -1) {
            this.id = previousId + 1;
        } else {
            this.id = 0;
        }

        previousId = this.id;
    }

    public Person(long id) {
        if (previousId != -1) {

        }

        this.id = id;
        previousId = id;
    }

    public void updateZipAndAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }
}
