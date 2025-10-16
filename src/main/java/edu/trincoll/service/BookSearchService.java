package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookSearchService {

    private final BookRepository bookRepository;
    /**
     * Constructs a new BookSearchService.
     * @param bookRepository The repository for accessing book data.
     */
    public BookSearchService(BookRepository bookRepository) {

        this.bookRepository = bookRepository;
    }
    /**
     * Searches for books by title.
     * @param title The title to search for (case-insensitive, partial match).
     * @return A list of books with matching titles, or empty list if none found.
     */
    public List<Book> searchByTitle(String title) {

        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    /**
     * Searches for books by author.
     * @param author The author name to search for.
     * @return A list of books by the specified author, or empty list if none found.
     */
    public List<Book> searchByAuthor(String author) {

        return bookRepository.findByAuthor(author);
    }
    /**
     * Searches for a book by ISBN.
     * @param isbn The ISBN to search for (exact match).
     * @return An Optional containing the book if found, or empty Optional if not found.
     */
    public Optional<Book> searchByIsbn(String isbn) {

        return bookRepository.findByIsbn(isbn);
    }
}