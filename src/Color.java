package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Color {
    public static final String SOURCE_FILE = "./resources/flower.png";
    public static final String DESTINATION_FILE = "./out/flower.png";
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();
        int numOfThreads = 5;
        recolorMultiThreaded(image, resultImage, numOfThreads);
        // 1개 -> 67ms
        // 2개 -> 44ms
        // 5개 -> 25ms
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "png", outputFile);
        System.out.println("duration = " + duration);
    }

    // 높이를 쓰레드 개수만큼 나누어서 처리해보기
    static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultImage, int numOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight() / numOfThreads;

        for (int i = 0; i < numOfThreads; i++) {
            final int threadMultiplier = i;

            Thread thread = new Thread(() -> {
                int leftColor = 0;
                int topColor = height * threadMultiplier;

                recolorImage(originalImage, resultImage, leftColor, topColor, width, height);
            });
            threads.add(thread); // 쓰레드 풀에 쓰레드 넣는 느낌
        }

        // 각 쓰레드 시작
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    // 색칠
    static void recolorImage(BufferedImage originalImage, BufferedImage resultImage,
                             int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < height; y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int blue = getBlue(rgb);
        int green = getGreen(rgb);

        int newRed;
        int newBlue;
        int newGreen;

        // 회색이라면 색 바꾸기
        if(isShadeOfGray(red, green, blue)) {
            // 최댓값 넘지 않게 min(255, newRed)
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 40);
            newBlue = Math.min(255, blue + 25);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = getRGBColor(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    // 같은 색상 강도를 갖는지 확인 -> 30차이가 적절
    static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(green - blue) < 30 && Math.abs(blue - red) < 30;
    }

    static int getRGBColor(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;

    }

    static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    static int getGreen(int rgb) {
        return  (rgb & 0x0000FF00) >> 8;
    }

    static int getBlue(int rgb) {
        return  rgb & 0x000000FF;
    }
}
