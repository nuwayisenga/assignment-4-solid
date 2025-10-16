package edu.trincoll.report;

import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class ReportGeneratorFactory {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public ReportGeneratorFactory(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public ReportGenerator getReportGenerator(String reportType) {
        return switch (reportType.toLowerCase()) {
            case "overdue" -> new OverdueReportGenerator(bookRepository);
            case "available" -> new AvailabilityReportGenerator(bookRepository);
            case "members" -> new MemberReportGenerator(memberRepository);
            default -> throw new IllegalArgumentException("Invalid report type: " + reportType);
        };
    }
}