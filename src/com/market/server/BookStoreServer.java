package com.market.server;

import com.google.gson.Gson;
import com.market.server.model.BookDTO;
import com.market.server.model.OrderDTO;
import com.market.server.model.OrderDTO.OrderItemDTO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookStoreServer {

    private static final int PORT = 7500;
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/books", new BookHandler());
        server.createContext("/orders", new OrderHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    static class BookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                handleGetBooks(exchange);
            } else if ("POST".equalsIgnoreCase(method)) {
                handleAddBook(exchange);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private void handleGetBooks(HttpExchange exchange) throws IOException {
            List<BookDTO> books = new ArrayList<>();
            String query = "SELECT * FROM book";

            try (Connection conn = DBHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    BookDTO book = new BookDTO(
                            rs.getString("book_id"),
                            rs.getString("name"),
                            rs.getInt("unit_price"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getString("release_date")
                    );
                    books.add(book);
                }
                String jsonResponse = gson.toJson(books);
                sendResponse(exchange, 200, jsonResponse);

            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Database error: " + e.getMessage());
            }
        }

        private void handleAddBook(HttpExchange exchange) throws IOException {
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            
            BookDTO newBook;
            try {
                 newBook = gson.fromJson(body, BookDTO.class);
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON format");
                return;
            }

            // Check existing
            try (Connection conn = DBHelper.getConnection()) {
                String checkQuery = "SELECT book_id FROM book WHERE book_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, newBook.getBookId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            sendResponse(exchange, 400, "{\"message\": \"Book ID already exists\"}");
                            return;
                        }
                    }
                }

                String insertQuery = "INSERT INTO book (book_id, name, unit_price, author, description, category, release_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setString(1, newBook.getBookId());
                    pstmt.setString(2, newBook.getName());
                    pstmt.setInt(3, newBook.getUnitPrice());
                    pstmt.setString(4, newBook.getAuthor());
                    pstmt.setString(5, newBook.getDescription());
                    pstmt.setString(6, newBook.getCategory());
                    pstmt.setString(7, newBook.getReleaseDate());
                    pstmt.executeUpdate();
                }

                sendResponse(exchange, 201, "{\"message\": \"success\", \"book\": " + gson.toJson(newBook) + "}");

            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
            }
        }
    }

    static class OrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleAddOrder(exchange);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private void handleAddOrder(HttpExchange exchange) throws IOException {
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            OrderDTO orderData;
            try {
                orderData = gson.fromJson(body, OrderDTO.class);
            } catch (Exception e) {
                sendResponse(exchange, 400, "Invalid JSON format");
                return;
            }

            Connection conn = null;
            try {
                conn = DBHelper.getConnection();
                conn.setAutoCommit(false); // Transaction start

                // Insert Order
                String insertOrderSql = "INSERT INTO orders (order_date, user_name, user_phone, user_address) VALUES (?, ?, ?, ?)";
                int orderId = -1;

                try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    String orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    pstmt.setString(1, orderDate);
                    pstmt.setString(2, orderData.getUser().getName());
                    pstmt.setString(3, orderData.getUser().getPhone());
                    pstmt.setString(4, orderData.getUser().getAddress());
                    pstmt.executeUpdate();

                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            orderId = rs.getInt(1);
                        }
                    }
                }

                if (orderId == -1) {
                    throw new SQLException("Failed to create order, no ID obtained.");
                }

                // Insert Order Items
                String insertItemSql = "INSERT INTO order_item (order_id, book_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                    for (OrderItemDTO item : orderData.getItems()) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setString(2, item.getBookId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();
                sendResponse(exchange, 201, "{\"message\": \"Order placed successfully\", \"orderId\": " + orderId + "}");

            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                sendResponse(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}

