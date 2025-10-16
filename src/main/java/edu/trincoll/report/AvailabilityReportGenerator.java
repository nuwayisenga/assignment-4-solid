package edu.trincoll.report;

import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;

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