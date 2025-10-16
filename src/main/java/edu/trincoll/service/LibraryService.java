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
 *
 * All SOLID violations have been fixed by extracting functionality
 * into focused services and using the Facade pattern for coordination.
 */
@Service
public class LibraryService {

    private final LibraryFacade libraryFacade;

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

    public String checkoutBook(String isbn, String memberEmail) {
        return libraryFacade.checkoutBook(isbn, memberEmail);
    }

    public String returnBook(String isbn) {
        return libraryFacade.returnBook(isbn);
    }

    public List<Book> searchBooks(String searchTerm, String searchType) {
        return libraryFacade.searchBooks(searchTerm, searchType);
    }

    public String generateReport(String reportType) {
        return libraryFacade.generateReport(reportType);
    }
}