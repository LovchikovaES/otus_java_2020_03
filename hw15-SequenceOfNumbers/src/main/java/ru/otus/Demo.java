package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demo {
    public static final int MAX_NUMBER = 10;
    public static final int MAX_REPEAT = 13;
    private static final Logger logger = LoggerFactory.getLogger(Demo.class);
    private int current = 0;

    public static void main(String[] args) throws InterruptedException {
        Demo demo = new Demo();
        Thread t1 = new Thread(demo::printNumbers);
        t1.setName("t1");
        t1.start();

        Thread t2 = new Thread(demo::printNumbers);
        t2.setName("t2");
        t2.start();

        t1.join();
        t2.join();
    }

    public synchronized void printNumbers() {
        int lastElement = 0;
        int repeatCount = 0;
        while (repeatCount < MAX_REPEAT) {
            try {
                while (lastElement < MAX_NUMBER) {
                    lastElement++;
                    logger.info(String.valueOf(lastElement));
                    this.current = this.current + 1;
                    notifyAll();
                    if (this.current % 2 == 0) {
                        // если номер текущей итерации общей по потокам кратен 2, то это поток 2
                        while (this.current % 2 == 0) {
                            this.wait();
                        }
                    } else {
                        // если номер текущей итерации общей по потокам не кратен 2, то это поток 1
                        while (this.current % 2 != 0) {
                            this.wait();
                        }
                    }
                }
                while (lastElement > 1) {
                    lastElement--;
                    logger.info(String.valueOf(lastElement));
                    this.current = this.current + 1;
                    notifyAll();
                    // На последней итерации не вызываем wait
                    if ((lastElement == 1) && (repeatCount + 1 == MAX_REPEAT)) {
                        continue;
                    }
                    if (this.current % 2 == 0) {
                        while (this.current % 2 == 0) {
                            this.wait();
                        }
                    } else {
                        while (this.current % 2 != 0) {
                            this.wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            repeatCount++;
        }
    }
}
