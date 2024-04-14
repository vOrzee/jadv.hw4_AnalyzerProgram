package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    static final int COUNT_TEXTS = 10000;
    static final int TEXT_LENGTH = 100000;
    static final int QUEUE_CAPACITY = 100;

    static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) throws InterruptedException {
        Thread fillingThread = getBuilderTextThread(COUNT_TEXTS, "abc", TEXT_LENGTH);
        Thread aCharacterThread = getCharacterCountingThread(queueA,'a',fillingThread);
        Thread bCharacterThread = getCharacterCountingThread(queueB,'b',fillingThread);
        Thread cCharacterThread = getCharacterCountingThread(queueC,'c',fillingThread);

        fillingThread.start();
        aCharacterThread.start();
        bCharacterThread.start();
        cCharacterThread.start();

        aCharacterThread.join();
        bCharacterThread.join();
        cCharacterThread.join();
    }

    public static Thread getBuilderTextThread(int countTexts, String letters, int textLength) {
        return new Thread(() -> {
            for (int i = 0; i < countTexts; i++) {
                String text = generateText(letters, textLength);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static Thread getCharacterCountingThread(BlockingQueue<String> queue, char character, Thread monitoring) {
        return new Thread(() -> {
            int max = 0;
            int count = 0;
            while (monitoring.isAlive() || !queue.isEmpty()) {
                try {
                    String text = queue.take();
                    for (char c : text.toCharArray()) {
                        if (c == character) {
                            count++;
                        }
                    }
                    if (count > max) {
                        max = count;
                    }
                    count = 0;
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("Максимальное количество символов \"" + character + "\": " + max);
        });
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}