package com.market.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.market.bookitem.Book;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookApiService {

    // TODO: 팀장 서버 주소로 바꿔줘야 함
    private static final String BASE_URL = "http://bookstore-db.gomtangmo.ru:5700";

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
}
