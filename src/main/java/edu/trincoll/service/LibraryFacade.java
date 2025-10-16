package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.model.Member;
import edu.trincoll.policy.CheckoutPolicy;
import edu.trincoll.policy.CheckoutPolicyFactory;
import edu.trincoll.policy.LateFeeCalculator;
import edu.trincoll.policy.LateFeeCalculatorFactory;
import edu.trincoll.report.ReportGenerator;
import edu.trincoll.report.ReportGeneratorFactory;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * AI Collaboration Summary:
 *
 * Team Members and Contributions:
 * - Gabriela Scavenius and Noella Uwayisenga: TODOs 1, 3, 4, 5, 6, 7, 8
 * - Chris Burns: TODO 2 (CheckoutPolicy)
 *
 * AI Tools Used: Claude (Anthropic)
 *
 * How AI Helped:
 * - Suggested Strategy pattern structure for checkout policies and late fee calculation
 * - Provided guidance on proper service extraction following Single Responsibility Principle
 * - Helped implement Dependency Inversion Principle with NotificationService interface
 * - Assisted with test coverage strategies and understanding SOLID principles
 * - Explained how each refactoring addresses specific SOLID violations
 *
 * What We Learned:
 * - Single Responsibility Principle leads to smaller, more focused classes that are easier to test
 * - Strategy pattern (OCP) makes adding new membership types trivial without modifying existing code
 * - Dependency Inversion Principle makes the system more flexible and testable with mock implementations
 * - Interface Segregation Principle prevents clients from depending on methods they don't use
 * - The Facade pattern provides a clean, simple API while hiding complex orchestration logic
 * - Hardest principle to apply was OCP - identifying what varies and abstracting it requires careful analysis
 */
@Service
public class LibraryFacade {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookService bookService;
    private final MemberService memberService;
    private final BookSearchService bookSearchService;
    private final NotificationService notificationService;
    private final ReportGeneratorFactory reportGeneratorFactory;

    public LibraryFacade(BookRepository bookRepository,
                         MemberRepository memberRepository,
                         BookService bookService,
                         MemberService memberService,
                         BookSearchService bookSearchService,
                         NotificationService notificationService,
                         ReportGeneratorFactory reportGeneratorFactory) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.bookService = bookService;
        this.memberService = memberService;
        this.bookSearchService = bookSearchService;
        this.notificationService = notificationService;
        this.reportGeneratorFactory = reportGeneratorFactory;
    }

    public String checkoutBook(String isbn, String memberEmail) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (!bookService.isAvailable(book)) {
            return "Book is not available";
        }

        CheckoutPolicy policy = CheckoutPolicyFactory.getCheckoutPolicy(member.getMembershipType());

        if (member.getBooksCheckedOut() >= policy.getMaxBooks()) {
            return "Member has reached checkout limit";
        }

        bookService.checkoutBook(book, member, policy.getLoanPeriodDays());
        memberService.incrementCheckoutCount(member);
        notificationService.sendCheckoutNotification(member, book, book.getDueDate());

        return "Book checked out successfully. Due date: " + book.getDueDate();
    }

    public String returnBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getStatus() != BookStatus.CHECKED_OUT) {
            return "Book is not checked out";
        }

        String memberEmail = book.getCheckedOutBy();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        double lateFee = 0.0;
        if (book.getDueDate().isBefore(LocalDate.now())) {
            long daysLate = LocalDate.now().toEpochDay() - book.getDueDate().toEpochDay();
            LateFeeCalculator feeCalculator = LateFeeCalculatorFactory.getLateFeeCalculator(
                    member.getMembershipType());
            lateFee = feeCalculator.calculateLateFee(daysLate);
        }

        bookService.returnBook(book);
        memberService.decrementCheckoutCount(member);
        notificationService.sendReturnNotification(member, book, lateFee);

        if (lateFee > 0) {
            return "Book returned. Late fee: $" + String.format("%.2f", lateFee);
        }

        return "Book returned successfully";
    }

    public List<Book> searchBooks(String searchTerm, String searchType) {
        if ("title".equalsIgnoreCase(searchType)) {
            return bookSearchService.searchByTitle(searchTerm);
        } else if ("author".equalsIgnoreCase(searchType)) {
            return bookSearchService.searchByAuthor(searchTerm);
        } else if ("isbn".equalsIgnoreCase(searchType)) {
            return bookSearchService.searchByIsbn(searchTerm)
                    .map(List::of)
                    .orElse(List.of());
        } else {
            throw new IllegalArgumentException("Invalid search type");
        }
    }

    public String generateReport(String reportType) {
        ReportGenerator generator = reportGeneratorFactory.getReportGenerator(reportType);
        return generator.generateReport();
    }
}