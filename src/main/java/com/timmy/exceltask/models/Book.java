package com.timmy.exceltask.models;


public class Book {
    private String author;
    private String bookName;
    private String isbn;
    private Integer pageNumber;

    public Book() {
    }

    public Book(String author, String bookName, String isbn, Integer pageNumber) {
        this.author = author;
        this.bookName = bookName;
        this.isbn = isbn;
        this.pageNumber = pageNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }


    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", bookName='" + bookName + '\'' +
                ", isbn='" + isbn + '\'' +
                ", pageNumber=" + pageNumber +
                '}';
    }
}
