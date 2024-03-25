import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static Thread generateText;


    public static void main(String[] args) throws InterruptedException {
        generateText = new Thread(() ->
        {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generateText.start();

        Thread threadA = getThread(queueA, 'a');
        Thread threadB = getThread(queueB, 'b');
        Thread threadC = getThread(queueC, 'c');

        threadA.start();
        threadB.start();
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();
    }

    private static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int max = countChar(queue, letter);
            System.out.println("Максимальное количество символов '" + letter + "' = " + max);
        });
    }

    public static int countChar(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            while (generateText.isAlive()) { //true, если поток не Terminated
                text = queue.take();
                for (char a : text.toCharArray()) {
                    if (a == letter) count++;
                }
                if (count > max) {
                    max = count;
                }
                count = 0;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return max;
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