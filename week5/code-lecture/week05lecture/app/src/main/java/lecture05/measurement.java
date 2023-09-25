package lecture05;

class measurement {
  public static void main(String[] args) { new measurement(); }
  
  public measurement () {
    long start= System.nanoTime();
    multiply(126465);
    long end= System.nanoTime();
    System.out.println(end-start+" ns");
  }

  private static int multiply(int i) {
    return i * i;
  } 
}