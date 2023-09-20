package exercises03;

class MyTuple2<T1, T2> {
    public T1 t1;
    public T2 t2;

    public MyTuple2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return t1 + " " + t2;
    }
}