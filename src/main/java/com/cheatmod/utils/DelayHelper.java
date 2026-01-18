package com.cheatmod.utils;

import java.util.Random;

public class DelayHelper {
    private static final Random random = new Random();
    
    public static void randomDelay(int minMs, int maxMs) {
        try {
            int delay = minMs + random.nextInt(maxMs - minMs + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void humanLikeDelay() {
        // Типичные человеческие задержки
        randomDelay(150, 300);
    }
    
    public static void thinkingDelay() {
        // Задержка как будто человек думает
        randomDelay(500, 1200);
    }
}