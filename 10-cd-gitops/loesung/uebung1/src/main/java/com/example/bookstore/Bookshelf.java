package com.example.bookstore;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Bookshelf {

    private final Set<Book> books = new HashSet<>();

    @PostConstruct
    public void initialize() {
        books.add(new Book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "0345391802"));
        books.add(new Book("The Martian", "Andy Weir", "0553418025"));
        books.add(new Book("Guards! Guards!", "Terry Pratchett", "0062225758"));
        books.add(new Book("Alice in Wonderland", "Lewis Carroll", "3458317422"));
        books.add(new Book("Life, the Universe and Everything", "Douglas Adams", "0345391829"));
    }

    public Collection<Book> findByTitle(String title) {
        if (title == null || title.isEmpty()) {
            return books;
        } else {
            return books
                    .stream()
                    .filter((Book b) -> b.getTitle().equalsIgnoreCase(title))
                    .collect(Collectors.toList());
        }
    }

    public Book findByIsbn(String isbn) {
        return books
                .stream()
                .filter((Book b) -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public void deleteByIsbn(String isbn) {
        books.removeIf(b -> b.getIsbn().equals(isbn));
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void updateBookByIsbn(String isbn, Book book) {
        for (Book existingBook : books) {
            if (existingBook.getIsbn().equals(isbn)) {
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
            }
        }
    }
}
