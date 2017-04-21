package geozombie.bboybboy.com.geozombie.j4f;

import java.util.Random;

final class RandomGenerator {
    private RandomGenerator() {

    }

    static int randomInRange(int min, int max) {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(max - min) + min;
    }

    static int randomWithMax(int max){
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(max);
    }
}