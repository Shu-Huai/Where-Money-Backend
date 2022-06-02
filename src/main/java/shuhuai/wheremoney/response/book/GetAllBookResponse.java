package shuhuai.wheremoney.response.book;

import shuhuai.wheremoney.entity.Book;

import java.util.List;

public class GetAllBookResponse {
    private List<Book> bookList;

    public GetAllBookResponse(List<Book> bookList) {
        this.bookList = bookList;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
}