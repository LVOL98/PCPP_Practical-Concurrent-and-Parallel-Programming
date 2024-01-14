//Exercise 10.?
//JSt vers Oct 23, 2023

//install  src/main/resources/english-words.txt
package exercises10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "/workspaces/PCPP_Practical-Concurrent-and-Parallel-Programming/week10/code-exercises/week10exercises/app/src/main/resources/english-words.txt";
    var stream = readWords(filename);
    // System.out.println(readWords(filename).count());

    // printFirstXWords(stream, 100);
    // printWordsOfGivenSize(stream, 22);
    // printFirstWordsOfGivenSize(stream, 22);
    // printPalindromes(stream);
    // printPalindromesParallels(stream);

    // System.out.println(readWordsFromUrl("https://staunstrups.dk/jst/english-words.txt").count());

    var stats = getStreamOfWordsLength(stream).summaryStatistics();
    System.out.println(String.format("min: %d, max: %d", stats.getMin(), stats.getMax()));
    System.out.println(stats.getAverage());
  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));

      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }

  public static boolean isPalindrome(String s) {
    return s.equals(new StringBuilder(s).reverse().toString());
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }

  public static void printFirstXWords(Stream<String> stream, int limit) {
    stream.limit(limit)
      .forEach(w -> System.out.println(w));
  }

  public static void printWordsOfGivenSize(Stream<String> stream, int wordSizeLimit) {
    stream.filter(w -> w.length() >= wordSizeLimit)
      .forEach(w -> System.out.println(w));
  }

  public static void printFirstWordsOfGivenSize(Stream<String> stream, int wordSizeLimit) {
    stream.filter(w -> w.length() >= wordSizeLimit)
      .findFirst()
      .ifPresent(System.out::println);
  }

  public static void printPalindromes(Stream<String> stream) {
    stream.filter(w -> isPalindrome(w))
      .forEach(System.out::println);
  }

  public static void printPalindromesParallels(Stream<String> stream) {
    stream.parallel()
      .filter(w -> isPalindrome(w))
      .forEach(System.out::println);
  }

  public static Stream<String> readWordsFromUrl(String url) {
    try {
      return new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream())).lines();
    } catch (IOException e) {
      return Stream.<String>empty();
    }
  }

  public static IntStream getStreamOfWordsLength(Stream<String> stream) {
    return stream.mapToInt(w -> w.length());
  }
}
