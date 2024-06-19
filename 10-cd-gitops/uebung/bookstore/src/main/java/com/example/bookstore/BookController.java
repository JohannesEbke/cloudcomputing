package com.example.bookstore;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final Bookshelf bookshelf;

    @Operation(summary = "Get all books", responses = {
            @ApiResponse(responseCode = "200", description = "List of books", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Book.class))
                    )
            }),
    })
    @GetMapping
    public Collection<Book> books(
            @RequestParam(value = "title", required = false, defaultValue = "") String title) {
        return bookshelf.findByTitle(title);
    }

    @Operation(summary = "Get a book with a specific ISBN", responses = {
            @ApiResponse(responseCode = "200", description = "Book found", content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Book.class)
                    )
            }),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = "text/plain")
            })
    })
    @GetMapping("/{isbn}")
    public Book byIsbn(@PathVariable("isbn") String isbn) {
        return bookshelf.findByIsbn(isbn);
    }

    @Operation(summary = "Delete a book with a specific ISBN")
    @DeleteMapping("/{isbn}")
    public void deleteByIsbn(@PathVariable("isbn") String isbn) {
        bookshelf.deleteByIsbn(isbn);
    }

    @Operation(summary = "Add a new book")
    @PostMapping("/")
    public void addBook(@RequestBody Book book) {
        bookshelf.addBook(book);
    }

    @Operation(summary = "Update a book with a specific ISBN")
    @PutMapping("/{isbn}")
    public void addBook(@PathVariable("isbn") String isbn, @RequestBody Book book) {
        bookshelf.updateBookByIsbn(isbn, book);
    }
}
