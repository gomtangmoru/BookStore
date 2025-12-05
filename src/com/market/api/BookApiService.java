package com.market.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.market.bookitem.Book;
import com.market.cart.CartItem;
import com.market.member.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookApiService {

    // TODO: 팀장 서버 주소로 바꿔줘야 함
    private static final String BASE_URL = "http://bookstore-db.gomtangmo.ru:7500";

    private static final Gson gson = new Gson();

    public static ArrayList<Book> fetchBooks() throws IOException {
        String url = BASE_URL + "/books";

        String json = HttpClient.get(url);

        Type listType = new TypeToken<List<Book>>() {}.getType();
        List<Book> list = gson.fromJson(json, listType);

        return new ArrayList<>(list);
    }

    public static void addBook(Book book) throws IOException {
        String url = BASE_URL + "/books";

        String jsonBody = gson.toJson(book);

        String response = HttpClient.postJson(url, jsonBody);

        System.out.println("addBook response = " + response);
    }
    public static void addOrder(User user, ArrayList<CartItem> cartItems) throws IOException {
        String url = BASE_URL + "/orders";

        // 1. 서버가 원하는 JSON 구조 만들기
        // {
        //   "user": { "name": "...", "phone": ... },
        //   "items": [ { "bookId": "...", "quantity": ... }, ... ]
        // }
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user", user);

        List<Map<String, Object>> items = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("bookId", item.getBookID());
            itemMap.put("quantity", item.getQuantity());
            items.add(itemMap);
        }
        orderData.put("items", items);

        // 2. JSON 변환 및 전송
        String jsonBody = gson.toJson(orderData);
        System.out.println("Sending Order JSON: " + jsonBody); // 디버깅용 로그

        String response = HttpClient.postJson(url, jsonBody);
        System.out.println("addOrder response = " + response);
    }
}

