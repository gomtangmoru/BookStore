package com.market.bookitem;

import java.io.IOException;
import java.util.ArrayList;

import com.market.api.BookApiService;

public class BookInIt {

    private static ArrayList<Book> mBookList = new ArrayList<>();
    private static int mTotalBook = 0;

    public static void init() {
        try {
            mBookList = BookApiService.fetchBooks();
            mTotalBook = mBookList.size();
        } catch (IOException e) {
            e.printStackTrace();
            mBookList = new ArrayList<>();
            mTotalBook = 0;
        }
    }

    public static ArrayList<Book> getmBookList() {
        return mBookList;
    }

    public static int getmTotalBook() {
        return mTotalBook;
    }
}
