package edu.trincoll.report;

import edu.trincoll.model.Book;
import edu.trincoll.repository.BookRepository;

import java.time.LocalDate;
import java.util.List;
/**
 * Report generator for overdue books.
 * Generates a detailed report of all books past their due date.
 */
public class OverdueReportGenerator implements ReportGenerator {

    private final BookRepository bookRepository;

    public OverdueReportGenerator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public String generateReport() {
        List<Book> overdueBooks = bookRepository.findByDueDateBefore(LocalDate.now());
        StringBuilder report = new StringBuilder("OVERDUE BOOKS REPORT\n");
        report.append("====================\n");
        for (Book book : overdueBooks) {
            report.append(String.format("%s by %s - Due: %s - Checked out by: %s\n",
                    book.getTitle(), book.getAuthor(), book.getDueDate(), book.getCheckedOutBy()));
        }
        return report.toString();
    }
}
