package com.bmo.threads.chapter8;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Random;
import java.util.StringJoiner;

public class MatrixGenerator {
    private static final String OUTPUT_FILE = "./resources/chapter8/matrices";
    private static final int N = 10;
    private static final int NUMBER_OF_MATRIX_PAIRS = 100000;

    public static void main(String[] args) throws IOException {
        File file = new File(OUTPUT_FILE);
        FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
        createMatrices(fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }

    private static float[] createRow(final Random random) {
        float[] row = new float[N];
        for (int i = 0; i < N; i++) {
            row[i] = random.nextFloat() * random.nextInt(100);
        }
        return row;
    }

    private static float[][] createMatrix(final Random random) {
        float[][] matrix = new float[N][N];
        for (int i = 0; i < N; i++) {
            matrix[i] = createRow(random);
        }
        return matrix;
    }

    private static void saveMatrixToFile(final float[][] matrix, final FileWriter fileWriter) throws IOException {
        for (int x = 0; x < N; x++) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            for (int y = 0; y < N; y++) {
                stringJoiner.add(String.format(Locale.US, "%.2f", matrix[x][y]));
            }
            fileWriter.write(stringJoiner.toString());
            fileWriter.write('\n');
        }
        fileWriter.write('\n');
    }

    private static void createMatrices(final FileWriter fileWriter) throws IOException {
        Random random = new Random();
        for (int i = 0; i < NUMBER_OF_MATRIX_PAIRS; i++) {
            float[][] matrix = createMatrix(random);
            saveMatrixToFile(matrix, fileWriter);
        }
    }
}
