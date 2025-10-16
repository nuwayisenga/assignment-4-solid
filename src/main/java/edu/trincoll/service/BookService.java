package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.model.Member;
import edu.trincoll.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
/**
 * Service for managing book checkout and return operations.
 * Handles book status updates and persistence.
 */
public class BookService {
    /**
     * Repository for book data access.
     */
    private final BookRepository bookRepository;
    /**
     * Constructs a new BookService.
     * @param bookRepository The repository for accessing book data.
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    /**
     * Checks out a book to a member with a specified loan period.
     * @param book The book to check out.
     * @param member The member checking out the book.
     * @param loanPeriodDays The number of days the book can be borrowed.
     */
    public void checkoutBook(Book book, Member member, int loanPeriodDays) {
        book.setStatus(BookStatus.CHECKED_OUT);
        book.setCheckedOutBy(member.getEmail());
        book.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
        bookRepository.save(book);
    }
    /**
     * Returns a book to the library and marks it as available.
     * @param book The book being returned.
     */
    public void returnBook(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        book.setCheckedOutBy(null);
        book.setDueDate(null);
        bookRepository.save(book);
    }

    /**
     * Checks if a book is available for checkout.
     * @param book The book to check.
     * @return True if the book is available, false otherwise.
     */
    public boolean isAvailable(Book book) {
        return book.getStatus() == BookStatus.AVAILABLE;
    }
}
