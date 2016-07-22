package com.lynbrookrobotics.sixteen.vision;

import java.util.Map.Entry;
import java.util.HashMap;

class Pair<A, B> {
  private A first;
  private B second;

  public Pair(A first, B second) {
    super();
    this.first = first;
    this.second = second;
  }

  public int hashCode() {
    int hashFirst = first != null ? first.hashCode() : 0;
    int hashSecond = second != null ? second.hashCode() : 0;

    return (hashFirst + hashSecond) * hashSecond + hashFirst;
  }

  public boolean equals(Object other) {
    if (other instanceof Pair) {
      @SuppressWarnings("rawtypes")
      Pair otherPair = (Pair) other;
      return
          ((this.first == otherPair.first ||
              (this.first != null && otherPair.first != null &&
                  this.first.equals(otherPair.first))) &&
              (this.second == otherPair.second ||
                  (this.second != null && otherPair.second != null &&
                      this.second.equals(otherPair.second))));
    }

    return false;
  }

  public String toString() {
    return "(" + first + ", " + second + ")";
  }

  public A getFirst() {
    return first;
  }

  public void setFirst(A first) {
    this.first = first;
  }

  public B getSecond() {
    return second;
  }

  public void setSecond(B second) {
    this.second = second;
  }
}


class Profiler {
  public static int accum = 0;

  private Profiler() {
  }

  private static final HashMap<String, Pair<Long, Long>> profilingData =
      new HashMap<String, Pair<Long, Long>>();

  private static void log(String msg) {
    System.out.println(msg);
  }

  public static void start(String methodName) {
    long startTime = System.nanoTime();

    if (!profilingData.containsKey(methodName))
      profilingData.put(methodName, new Pair<Long, Long>(0l, 0l));

    profilingData.get(methodName).setFirst(startTime);

  }

  public static void end(String methodName) {
    long endTime = System.nanoTime();

    profilingData.get(methodName).setSecond(endTime);
  }

  public static void show(String methodName) {
    log("Profiler output:");

    long endTime = profilingData.get(methodName).getSecond();
    long startTime = profilingData.get(methodName).getFirst();

    long totalTime = (endTime - startTime) / 1000000;

    log("\tMethod name: " + methodName);
    log("\tTime taken: " + totalTime + "ms");

    clear();
  }

  public static void show() {
    log("Profiler output:");
    for (Entry<String, Pair<Long, Long>> e : profilingData.entrySet()) {
      long endTime = e.getValue().getSecond();
      long startTime = e.getValue().getFirst();

      long totalTime = (endTime - startTime) / 1000000;

      log("\tMethod name: " + e.getKey());
      log("\tTime taken: " + totalTime + " ms");
    }
    clear();
  }

  public static void clear() {
    profilingData.clear();
  }
}
