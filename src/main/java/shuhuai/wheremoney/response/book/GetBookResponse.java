package shuhuai.wheremoney.response.book;

import shuhuai.wheremoney.entity.Book;

public class GetBookResponse {
    private Book book;

    public GetBookResponse(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}