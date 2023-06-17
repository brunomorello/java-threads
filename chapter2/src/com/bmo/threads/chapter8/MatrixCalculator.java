package com.bmo.threads.chapter8;

import java.io.*;
import java.util.*;

public class MatrixCalculator {

    private static final int N = 10;

    private static final String INPUT_FILE = "./resources/chapter8/matrices";
    private static final String OUTPUT_FILE = "./resources/chapter8/matrices_result.txt";

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierProducer matricesMultiplierProducer = new MatricesMultiplierProducer(threadSafeQueue, new FileWriter(outputFile));

        matricesReaderProducer.start();
        matricesMultiplierProducer.start();
    }

    private static class MatricesMultiplierProducer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;

        public MatricesMultiplierProducer(ThreadSafeQueue queue, FileWriter fileWriter) {
            this.queue = queue;
            this.fileWriter = fileWriter;
        }

        @Override
        public void run() {
            while (true) {
                MatrixPair matrixPair = queue.remove();
                if (matrixPair == null) {
                    System.out.println("No more matrices to calc");
                    break;
                }

                float[][] result = multiplyMatrices(matrixPair.matrix1, matrixPair.matrix2);
                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format(Locale.US, "%.2f", matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            fileWriter.write('\n');
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float[][] result = new float[N][N];
            for(int row = 0; row < N; row++) {
                for (int column = 0; column < N; column++) {
                    for (int k = 0; k < N; k++) {
                        result[row][column] = m1[row][k] * m2[k][column];
                    }
                }
            }
            return result;
        }
    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(final FileReader fileReader, final ThreadSafeQueue queue) {
            this.queue = queue;
            this.scanner = new Scanner(fileReader);
        }
        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int row = 0; row < N; row++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] currentRow = scanner.nextLine().split(", ");
                for (int column = 0; column < N; column++) {
                    matrix[row][column] = Float.valueOf(currentRow[column]);
                }
            }
            scanner.nextLine();
            return matrix;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();

                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer Thread is terminating");
                    return;
                }

                MatrixPair matrixPair = new MatrixPair();
                matrixPair.matrix1 = matrix1;
                matrixPair.matrix2 = matrix2;

                queue.add(matrixPair);
            }
        }
    }

    private static class ThreadSafeQueue {
        private Queue<MatrixPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;

        private static final int CAPACITY = 5;

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

        public synchronized MatrixPair remove() {
            MatrixPair matrixPair = null;
            while (isEmpty && !isTerminate) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

            if (queue.size() == 1) {
                isEmpty = true;
            }

            if (queue.size() == 0 && isTerminate) {
                return null;
            }

            System.out.println("queue size is " + queue.size());

            matrixPair = queue.remove();
            if (queue.size() == CAPACITY -1) {
                notifyAll();
            }
            return matrixPair;
        }

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
