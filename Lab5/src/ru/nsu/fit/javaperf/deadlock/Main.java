package ru.nsu.fit.javaperf.deadlock;

public class Main {

  static final Object lock = new Object();

  static final MyCountdownLatch latch = new MyCountdownLatch(2);

  public static void main(String[] args) {
    Thread thread1 = new Deadlock();
    Thread thread2 = new Deadlock();
    thread1.start();
    thread2.start();
  }

  private static class Deadlock extends Thread {

    @Override
    public void run() {
      System.out.println("Thread started");
      synchronized (lock) {
        latch.countDown();
        try {
          latch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      System.out.println("Thread finished");
    }
  }

  private static class MyCountdownLatch {
    private int count;

    public MyCountdownLatch(int count) {
      this.count = count;
    }

    public void await() throws InterruptedException {
      synchronized (this) {
        if (count > 0) {
          this.wait();
        }
      }
    }

    public void countDown() {
      synchronized (this) {
        count--;
        if (count == 0) {
          this.notifyAll();
        }
      }
    }
  }
}
