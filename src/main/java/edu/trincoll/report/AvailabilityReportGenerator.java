package edu.trincoll.report;

import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;
/**
 * Report generator for book availability statistics.
 * Generates a report showing the count of available books.
 */
public class AvailabilityReportGenerator implements ReportGenerator {

    private final BookRepository bookRepository;

    public AvailabilityReportGenerator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public String generateReport() {
        long availableCount = bookRepository.countByStatus(BookStatus.AVAILABLE);
        return "Available books: " + availableCount;
    }
}