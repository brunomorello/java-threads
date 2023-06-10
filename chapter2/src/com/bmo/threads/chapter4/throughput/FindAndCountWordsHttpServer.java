package com.bmo.threads.chapter4.throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FindAndCountWordsHttpServer {
    private static final String INPUT_FILE = "./resources/chapter4/war_and_peace.txt";
    private static final int NUM_OF_TREADS = 1;

    public static void main(String[] args) throws IOException {
        String book = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(book);
    }

    public static void startServer(String book) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(book));

        Executor executor = Executors.newFixedThreadPool(NUM_OF_TREADS);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {

        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String word = keyValue[1];

            if (!action.equals("word")) {
                exchange.sendResponseHeaders(400, 0);
            }

            long countWord = countWord(word);

            byte[] resp = Long.toString(countWord).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resp.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(resp);
            outputStream.close();
        }

        private long countWord(String word) {
            long count = 0;
            int i = 0;

            while (i >= 0) {
                i = text.indexOf(word, i);

                if (i >= 0) {
                    count++;
                    i++;
                }
            }
            return count;
        }
    }
}
