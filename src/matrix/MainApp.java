package src.matrix;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MainApp {
    private static final String INPUT_FILE = "./out/matrics";
    private static final String OUTPUT_FILE = "./out/matrices_results.txt";
    private static final int N = 10;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer matricesReader = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierConsumer matricesConsumer = new MatricesMultiplierConsumer(new FileWriter(outputFile), threadSafeQueue);

        matricesConsumer.start();
        matricesReader.start();
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;

        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.fileWriter = fileWriter;
            this.queue = queue;
        }

        private static void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            fileWriter.write('\n');
        }

        @Override
        public void run() {
            while (true) {
                MatrixPair matricesPair = queue.use();
                if (matricesPair == null) {
                    System.out.println("더 이상 읽을 행렬이 없어요. 생산자 쓰레드가 종료됩니다.");
                    break;
                }

                float[][] result = multiplyMatrices(matricesPair.matrix1, matricesPair.matrix2);

                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                }
            }

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float[][] result = new float[N][N];
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int k = 0; k < N; k++) {
                        result[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }
            return result;
        }
    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();
                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("더 이상 읽을 행렬이 없어요. 소비자 쓰레드가 종료됩니다.");
                    return;
                }

                MatrixPair matricesPair = new MatrixPair();
                matricesPair.matrix1 = matrix1;
                matricesPair.matrix2 = matrix2;

                queue.add(matricesPair);
            }
        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");
                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    private static class ThreadSafeQueue {
        private Queue<MatrixPair> queue = new LinkedList<>();
        private boolean isEmpty = true; // 큐에 행렬이 있는지
        private boolean isTerminate = false; // 소비자 쓰레드가 종료 확인
        private static final int CAPACITY = 5; // 큐의 최적 용량

        // 생산자
        public synchronized void add(MatrixPair matrixPair) {
            while (queue.size() == CAPACITY) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            queue.add(matrixPair);
            isEmpty = false;
            notify();
        }
        // 소비자
        public synchronized MatrixPair use() {
            MatrixPair matrixPair = null;
            while (isEmpty && !isTerminate) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

            if (queue.size() == 1) {
                // 소비
                isEmpty = true;
            }
            if (queue.size() == 0) {
                return null;
            }

            System.out.println("queue size = " + queue.size());

            matrixPair = queue.remove();
            if (queue.size() == CAPACITY -1) {
                notifyAll();
            }
            return matrixPair;
        }

        // 큐가 비면 소비자가 쓰레드를 종료해야한다는 것을 전달 -> 생산자가 호출
        public synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }

    private static class MatrixPair {
        public float[][] matrix1;
        public float[][] matrix2;
    }
}
