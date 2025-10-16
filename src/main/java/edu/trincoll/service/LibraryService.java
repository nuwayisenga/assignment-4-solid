package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import edu.trincoll.report.ReportGeneratorFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Legacy LibraryService that delegates to LibraryFacade.
 * Maintains backward compatibility with existing tests.
 * All SOLID violations have been fixed by extracting functionality
 * into focused services and using the Facade pattern for coordination.
 */
@Service
public class LibraryService {
    /**
     * The facade that coordinates all library operations.
     */
    private final LibraryFacade libraryFacade;
    /**
     * Constructs a new LibraryService with the required repositories.
     * Creates and wires all services needed by the facade.
     * @param bookRepository The repository for accessing book data.
     * @param memberRepository The repository for accessing member data.
     */
    public LibraryService(BookRepository bookRepository,
                          MemberRepository memberRepository) {
        // Create all the services that LibraryFacade needs
        BookService bookService = new BookService(bookRepository);
        MemberService memberService = new MemberService(memberRepository);
        BookSearchService bookSearchService = new BookSearchService(bookRepository);
        NotificationService notificationService = new EmailNotificationService();
        ReportGeneratorFactory reportGeneratorFactory = new ReportGeneratorFactory(bookRepository, memberRepository);

        // Create the facade
        this.libraryFacade = new LibraryFacade(
                bookRepository,
                memberRepository,
                bookService,
                memberService,
                bookSearchService,
                notificationService,
                reportGeneratorFactory
        );
    }
    /**
     * Checks out a book to a library member.
     * @param isbn The ISBN of the book to check out.
     * @param memberEmail The email address of the member checking out the book.
     * @return A success message, or an error message if checkout fails.
     */
    public String checkoutBook(String isbn, String memberEmail) {
        return libraryFacade.checkoutBook(isbn, memberEmail);
    }
    /**
     * Returns a book to the library.
     * @param isbn The ISBN of the book being returned.
     * @return A success message, or an error message if return fails.
     */
    public String returnBook(String isbn) {
        return libraryFacade.returnBook(isbn);
    }
    /**
     * Searches for books based on specified criteria.
     * @param searchTerm The text to search for.
     * @param searchType The type of search ("title", "author", "isbn", or "available").
     * @return A list of books matching the criteria, or empty list if none found.
     */
    public List<Book> searchBooks(String searchTerm, String searchType) {
        return libraryFacade.searchBooks(searchTerm, searchType);
    }

    /**
     * Generates a report about the library's current state.
     * @param reportType The type of report ("inventory", "members", or "overdue").
     * @return A formatted string containing the report, or error message if invalid type.
     */
    public String generateReport(String reportType) {
        return libraryFacade.generateReport(reportType);
    }
}