/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author thien
 */
public class FavoriteBook {

    private int favoriteId;
    private int userId;
    private int bookId;
    private Book book;

    public FavoriteBook() {
    }

    public FavoriteBook(int favoriteId, int userId, int bookId, Book book) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.bookId = bookId;
        this.book = book;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
    
    
}
