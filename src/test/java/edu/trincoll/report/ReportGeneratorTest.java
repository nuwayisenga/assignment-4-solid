package edu.trincoll.report;

import edu.trincoll.model.Book;
import edu.trincoll.model.BookStatus;
import edu.trincoll.repository.BookRepository;
import edu.trincoll.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Report Generator Tests")
class ReportGeneratorTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    private ReportGeneratorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ReportGeneratorFactory(bookRepository, memberRepository);
    }

    @Test
    @DisplayName("Should generate overdue books report")
    void shouldGenerateOverdueReport() {
        Book overdueBook = new Book("978-0-123456-78-9", "Test Book", "Test Author", LocalDate.now());
        overdueBook.setStatus(BookStatus.CHECKED_OUT);
        overdueBook.setDueDate(LocalDate.now().minusDays(5));
        overdueBook.setCheckedOutBy("test@example.com");

        when(bookRepository.findByDueDateBefore(LocalDate.now()))
                .thenReturn(List.of(overdueBook));

        ReportGenerator generator = factory.getReportGenerator("overdue");
        String report = generator.generateReport();

        assertThat(report).contains("OVERDUE BOOKS REPORT");
        assertThat(report).contains("Test Book");
        assertThat(report).contains("Test Author");
    }

    @Test
    @DisplayName("Should generate availability report")
    void shouldGenerateAvailabilityReport() {
        when(bookRepository.countByStatus(BookStatus.AVAILABLE)).thenReturn(42L);

        ReportGenerator generator = factory.getReportGenerator("available");
        String report = generator.generateReport();

        assertThat(report).isEqualTo("Available books: 42");
    }

    @Test
    @DisplayName("Should generate member report")
    void shouldGenerateMemberReport() {
        when(memberRepository.count()).thenReturn(100L);

        ReportGenerator generator = factory.getReportGenerator("members");
        String report = generator.generateReport();

        assertThat(report).isEqualTo("Total members: 100");
    }

    @Test
    @DisplayName("Should throw exception for invalid report type")
    void shouldThrowExceptionForInvalidReportType() {
        assertThatThrownBy(() -> factory.getReportGenerator("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid report type");
    }
}