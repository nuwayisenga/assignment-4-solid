# Refactoring Report: SOLID Principles

## Team Information
- **Team Members:** Gabriela Scavenius, Noella Uwayisenga, Chris G Burns
- **Date:** October 16, 2025
- **Assignment:** Assignment 4 - SOLID Principles with Spring Data JPA

---

## Executive Summary

This document describes the comprehensive refactoring of the Library Management System from a monolithic service with multiple SOLID violations into a clean, maintainable architecture following all five SOLID principles.

**Key Metrics:**
- **Lines of code refactored:** ~500+
- **New classes/interfaces created:** 23
- **Test coverage improvement:** 62% → 86%
- **Services extracted:** 6 (BookService, MemberService, BookSearchService, NotificationService, LibraryFacade, plus existing LibraryService)
- **Strategy patterns implemented:** 2 (CheckoutPolicy, LateFeeCalculator)
- **Report generators:** 3 implementations with factory pattern

---

## Single Responsibility Principle (SRP)

### Violation

The original `LibraryService` class had multiple responsibilities:
- Book operations (checkout, return, availability checking)
- Member operations (updating checkout counts, validation)
- Notifications (sending checkout/return messages via console)
- Search operations (finding books by title, author, ISBN)
- Reporting (generating overdue, availability, and member reports)

**Problem:** A single class doing too many things makes it:
- Hard to test (need to mock many dependencies)
- Hard to maintain (changes ripple across unrelated functionality)
- Hard to understand (too many lines of code with mixed concerns)

### Our Solution

We extracted five separate services, each with a single, well-defined responsibility:

1. **BookService** - Manages book state and persistence
2. **MemberService** - Manages member state and persistence
3. **NotificationService** - Handles all user notifications
4. **BookSearchService** - Performs book search operations
5. **LibraryFacade** - Coordinates between services (Facade pattern)

### Code Example

**Before (SRP Violation):**
```java
public class LibraryService {
    public String checkoutBook(String isbn, String memberEmail) {
        // Find book (database logic)
        Book book = bookRepository.findByIsbn(isbn)...
        
        // Find member (database logic)
        Member member = memberRepository.findByEmail(memberEmail)...
        
        // Update book (book logic)
        book.setStatus(BookStatus.CHECKED_OUT);
        bookRepository.save(book);
        
        // Update member (member logic)
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        memberRepository.save(member);
        
        // Send notification (notification logic)
        System.out.println("Sending email to: " + member.getEmail());
        
        // All mixed together in one method!
    }
}
```

**After (SRP Compliant):**
```java
@Service
public class BookService {
    private final BookRepository bookRepository;
    
    public void checkoutBook(Book book, Member member, int loanPeriodDays) {
        // Only book-related operations
        book.setStatus(BookStatus.CHECKED_OUT);
        book.setCheckedOutBy(member.getEmail());
        book.setDueDate(LocalDate.now().plusDays(loanPeriodDays));
        bookRepository.save(book);
    }
}

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    
    public void incrementCheckoutCount(Member member) {
        // Only member-related operations
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        memberRepository.save(member);
    }
}

@Service
public class EmailNotificationService implements NotificationService {
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        // Only notification logic
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book checked out");
        System.out.println("Message: You have checked out " + book.getTitle());
    }
}
```

### Why This Is Better

- ✅ **Easier to test:** Each service can be tested independently with focused unit tests
- ✅ **Easier to maintain:** Changes to book logic don't affect member or notification logic
- ✅ **Easier to understand:** Each class has a clear, single purpose
- ✅ **Reusable:** Services can be used in different contexts (e.g., MemberService in a mobile app)
- ✅ **Follows Unix philosophy:** "Do one thing and do it well"

---

## Open-Closed Principle (OCP)

### Violation

The original code used if-else statements to handle different membership types:

**Checkout Limits:**
```java
if (member.getMembershipType() == MembershipType.REGULAR) {
    maxBooks = 3;
    loanPeriodDays = 14;
} else if (member.getMembershipType() == MembershipType.PREMIUM) {
    maxBooks = 10;
    loanPeriodDays = 30;
} else if (member.getMembershipType() == MembershipType.STUDENT) {
    maxBooks = 5;
    loanPeriodDays = 21;
}
// Adding new membership types requires modifying this code!
```

**Late Fee Calculation:**
```java
if (member.getMembershipType() == MembershipType.REGULAR) {
    lateFee = daysLate * 0.50;
} else if (member.getMembershipType() == MembershipType.PREMIUM) {
    lateFee = 0.0;
} else if (member.getMembershipType() == MembershipType.STUDENT) {
    lateFee = daysLate * 0.25;
}
// More modification needed here too!
```

**Problem:** Every time we want to add a new membership type (e.g., "Faculty" or "Senior"), we must:
- Modify existing code
- Risk breaking existing functionality
- Violate the "closed for modification" principle

### Our Solution

We implemented the **Strategy Pattern** with two separate strategy hierarchies:

1. **CheckoutPolicy Strategy** - For checkout limits and loan periods
2. **LateFeeCalculator Strategy** - For late fee calculations

### Code Example

**Strategy Interfaces:**
```java
public interface CheckoutPolicy {
    int getMaxBooks();
    int getLoanPeriodDays();
    boolean canCheckout(Member member);
}

public interface LateFeeCalculator {
    double calculateLateFee(long daysLate);
}
```

**Concrete Implementations:**
```java
public class RegularPolicy implements CheckoutPolicy {
    private static final int MAX_BOOKS = 3;
    private static final int LOAN_PERIOD_DAYS = 14;
    
    @Override
    public int getMaxBooks() { return MAX_BOOKS; }
    
    @Override
    public int getLoanPeriodDays() { return LOAN_PERIOD_DAYS; }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < MAX_BOOKS;
    }
}

public class PremiumPolicy implements CheckoutPolicy {
    private static final int MAX_BOOKS = 10;
    private static final int LOAN_PERIOD_DAYS = 30;
    
    @Override
    public int getMaxBooks() { return MAX_BOOKS; }
    
    @Override
    public int getLoanPeriodDays() { return LOAN_PERIOD_DAYS; }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < MAX_BOOKS;
    }
}

// Similar implementations for StudentPolicy, RegularLateFeeCalculator, etc.
```

**Factory for Strategy Selection:**
```java
@Component
public class CheckoutPolicyFactory {
    public static CheckoutPolicy getCheckoutPolicy(MembershipType type) {
        return switch (type) {
            case REGULAR -> new RegularPolicy();
            case PREMIUM -> new PremiumPolicy();
            case STUDENT -> new StudentPolicy();
        };
    }
}
```

**Usage in LibraryFacade:**
```java
CheckoutPolicy policy = CheckoutPolicyFactory.getCheckoutPolicy(member.getMembershipType());
int loanPeriodDays = policy.getLoanPeriodDays();
```

### Why This Is Better

- ✅ **Open for extension:** Add new membership types by creating new classes (e.g., `FacultyPolicy`)
- ✅ **Closed for modification:** Don't need to modify existing policy classes
- ✅ **Testable:** Each policy can be tested independently
- ✅ **Clear separation:** Each membership type's rules are in one place

**Example - Adding a new "Faculty" membership type:**
```java
// Just create a new class - no modifications to existing code!
public class FacultyPolicy implements CheckoutPolicy {
    @Override
    public int getMaxBooks() { return 15; }
    
    @Override
    public int getLoanPeriodDays() { return 45; }
    
    @Override
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < 15;
    }
}

// Add one case to the factory
case FACULTY -> new FacultyPolicy();
```

---

## Liskov Substitution Principle (LSP)

### Analysis

While the original code didn't directly violate LSP, our refactoring **enables** LSP compliance:

**Strategy Pattern and LSP:**
```java
// Any CheckoutPolicy implementation can be substituted
CheckoutPolicy policy = getPolicy(); // Could be Regular, Premium, or Student
int maxBooks = policy.getMaxBooks(); // Always works correctly
```

All implementations of `CheckoutPolicy` and `LateFeeCalculator` can be used interchangeably without breaking the system. Each implementation fulfills the contract of its interface.

**ReportGenerator and LSP:**
```java
// Any ReportGenerator can be used interchangeably
ReportGenerator generator = factory.getReportGenerator(reportType);
String report = generator.generateReport(); // Always returns a valid report
```

### Why This Matters

- ✅ **Polymorphism works correctly:** Clients can use base types without knowing concrete types
- ✅ **Substitutability:** Any implementation can replace another
- ✅ **Behavioral consistency:** All implementations follow the same contracts

---

## Interface Segregation Principle (ISP)

### Violation

If we had extracted the original monolithic `LibraryService` into a single interface, it would have been a "fat interface":
```java
// BAD: Fat interface
public interface LibraryOperations {
    String checkoutBook(String isbn, String memberEmail);
    String returnBook(String isbn);
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    Optional<Book> searchByIsbn(String isbn);
    String generateReport(String reportType);
    // Clients forced to depend on ALL methods, even if they only need one!
}
```

**Problem:** A controller that only needs search functionality would depend on checkout, return, and reporting methods it never uses.

### Our Solution

We created **focused, segregated interfaces** and services:
```java
// Focused service for book operations
@Service
public class BookService {
    void checkoutBook(Book book, Member member, int loanPeriodDays);
    void returnBook(Book book);
    boolean isAvailable(Book book);
}

// Focused service for search operations
@Service
public class BookSearchService {
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    Optional<Book> searchByIsbn(String isbn);
}

// Focused service for member operations
@Service
public class MemberService {
    void incrementCheckoutCount(Member member);
    void decrementCheckoutCount(Member member);
}
```

### Code Example

**Client that only needs search:**
```java
@RestController
public class SearchController {
    private final BookSearchService searchService; // Only depends on search!
    
    @GetMapping("/search")
    public List<Book> search(@RequestParam String title) {
        return searchService.searchByTitle(title);
    }
}
```

**Client that only needs checkout:**
```java
@RestController
public class CheckoutController {
    private final LibraryFacade facade; // Uses facade for complex operations
    
    @PostMapping("/checkout")
    public String checkout(@RequestBody CheckoutRequest request) {
        return facade.checkoutBook(request.getIsbn(), request.getEmail());
    }
}
```

### Why This Is Better

- ✅ **Minimal dependencies:** Clients only depend on what they need
- ✅ **Easier to mock:** In tests, only mock the interface you're using
- ✅ **Better encapsulation:** Each service exposes only related operations
- ✅ **Flexible composition:** Mix and match services as needed

---

## Dependency Inversion Principle (DIP)

### Violation

The original code had direct dependencies on concrete implementations:
```java
// High-level module depends on low-level implementation detail
System.out.println("Sending email to: " + member.getEmail());
// What if we want to send SMS? Push notifications? We'd have to change the code!
```

**Problem:** The high-level business logic (checkout/return) was tightly coupled to low-level notification details (console output).

### Our Solution

We created an **abstraction** (interface) and made the high-level module depend on it:

**Interface (Abstraction):**
```java
public interface NotificationService {
    void sendCheckoutNotification(Member member, Book book, LocalDate dueDate);
    void sendReturnNotification(Member member, Book book, double lateFee);
}
```

**Concrete Implementation:**
```java
@Service
public class EmailNotificationService implements NotificationService {
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        // Current implementation uses console
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book checked out");
        System.out.println("Message: You have checked out " + book.getTitle());
    }
    
    @Override
    public void sendReturnNotification(Member member, Book book, double lateFee) {
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book returned");
        System.out.println("Message: You have returned " + book.getTitle());
    }
}
```

**High-level module depends on abstraction:**
```java
@Service
public class LibraryFacade {
    private final NotificationService notificationService; // Depends on interface!
    
    public String checkoutBook(String isbn, String memberEmail) {
        // ... business logic ...
        notificationService.sendCheckoutNotification(member, book, book.getDueDate());
        // LibraryFacade doesn't know or care HOW notifications are sent!
    }
}
```

### Why This Is Better

- ✅ **Flexibility:** Swap implementations without changing high-level code
- ✅ **Testability:** Easy to provide mock notifications in tests
- ✅ **Extensibility:** Add new notification types (SMS, push, Slack) without modifying LibraryFacade

**Example - Adding SMS Notifications:**
```java
@Service
public class SmsNotificationService implements NotificationService {
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        // New implementation - no changes to LibraryFacade needed!
        smsClient.send(member.getPhone(), "You checked out: " + book.getTitle());
    }
}

// Switch implementations in configuration - zero code changes!
```

---

## Architecture Comparison

### Before Refactoring
```
┌─────────────────────────────────────┐
│       LibraryService                │
│  (God Object - does everything)     │
│                                     │
│  - Book operations                  │
│  - Member operations                │
│  - Notifications                    │
│  - Search                           │
│  - Reports                          │
│  - if-else for membership types     │
└─────────────────────────────────────┘
         ↓ directly accesses
    ┌────────┴─────────┐
    ↓                  ↓
BookRepository    MemberRepository
```

**Problems:**
- 200+ lines in one class
- Hard to test (many responsibilities)
- Hard to extend (must modify existing code)
- Tight coupling

### After Refactoring
```
                ┌──────────────────────┐
                │   LibraryService     │ ← Delegates to facade
                │   (Compatibility)    │
                └──────────┬───────────┘
                           ↓
                ┌──────────────────────┐
                │   LibraryFacade      │ ← Orchestrates services
                │   (Coordinator)      │
                └──────────┬───────────┘
                           ↓
        ┌──────────────────┼──────────────────┐
        ↓                  ↓                  ↓
┌───────────────┐  ┌───────────────┐  ┌──────────────┐
│ BookService   │  │ MemberService │  │ BookSearch   │
│               │  │               │  │ Service      │
└───────┬───────┘  └───────┬───────┘  └──────┬───────┘
        ↓                  ↓                  ↓
   BookRepository    MemberRepository   BookRepository
   
        ┌──────────────────┴──────────────────┐
        ↓                                     ↓
┌──────────────────┐              ┌──────────────────┐
│ Notification     │              │ ReportGenerator  │
│ Service          │              │ Factory          │
│ (Interface)      │              └──────────────────┘
└──────────────────┘                       ↓
        ↓                          ┌────────┴────────┐
┌──────────────────┐              ↓                 ↓
│ Email            │     OverdueReport      AvailabilityReport
│ Notification     │     Generator          Generator
│ Service          │
└──────────────────┘

        ┌──────────────────┴──────────────────┐
        ↓                                     ↓
┌──────────────────┐              ┌──────────────────┐
│ CheckoutPolicy   │              │ LateFeeCalculator│
│ Factory          │              │ Factory          │
└──────────────────┘              └──────────────────┘
        ↓                                     ↓
┌───────┴───────┐                  ┌─────────┴────────┐
RegularPolicy    PremiumPolicy     RegularCalculator  PremiumCalculator
StudentPolicy                      StudentCalculator
```

**Benefits:**
- Clear separation of concerns
- Each service is 20-50 lines
- Easy to test with mocks
- Easy to extend (add new policies/reports)
- Loose coupling

---

## Testing Improvements

### Before Refactoring

**Testing the monolithic service required mocking everything:**
```java
@Mock BookRepository bookRepository;
@Mock MemberRepository memberRepository;
@InjectMocks LibraryService service;

// Hard to test notification logic in isolation
// Hard to test policy logic in isolation
// Tests are complex and fragile
```

### After Refactoring

**Testing is much simpler:**

**Unit Test Example - BookService:**
```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookRepository bookRepository;
    @InjectMocks private BookService bookService;
    
    @Test
    void shouldCheckoutBook() {
        Book book = new Book(...);
        Member member = new Member(...);
        
        bookService.checkoutBook(book, member, 14);
        
        assertThat(book.getStatus()).isEqualTo(BookStatus.CHECKED_OUT);
        verify(bookRepository).save(book);
    }
}
```

**Unit Test Example - CheckoutPolicy:**
```java
class CheckoutPolicyTest {
    @Test
    void regularPolicyShouldHaveCorrectLimits() {
        CheckoutPolicy policy = new RegularPolicy();
        
        assertThat(policy.getMaxBooks()).isEqualTo(3);
        assertThat(policy.getLoanPeriodDays()).isEqualTo(14);
    }
}
```

**Integration Test Example - LibraryFacade:**
```java
@ExtendWith(MockitoExtension.class)
class LibraryFacadeTest {
    @Mock BookRepository bookRepository;
    @Mock MemberRepository memberRepository;
    // ... other mocks
    
    LibraryFacade facade;
    
    @BeforeEach
    void setUp() {
        // Compose the facade with real services and mocked repositories
        facade = new LibraryFacade(...);
    }
    
    @Test
    void shouldCheckoutBookWithAllServices() {
        // Test the full orchestration
    }
}
```

### Test Coverage Metrics

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Overall Coverage | 62% | 86% | +24% |
| Service Coverage | 62% | 85% | +23% |
| Policy Coverage | N/A | 77% | New |
| Report Coverage | N/A | 100% | New |
| Total Tests | 10 | 26 | +160% |

---

## Key Learnings

### 1. Single Responsibility Principle

**Learning:** SRP is about having one reason to change. Each class should do one thing well.

**Challenge:** It's tempting to put related functionality in one class for convenience, but this creates maintenance nightmares.

**Benefit:** Small, focused classes are easier to understand, test, and maintain.

### 2. Open-Closed Principle

**Learning:** OCP is about identifying what varies and abstracting it behind interfaces.

**Challenge:** **This was the hardest principle to apply.** You need to:
- Anticipate future changes
- Identify the "variation points" in your code
- Create appropriate abstractions without over-engineering

**Benefit:** Adding new features becomes trivial. We can add new membership types in minutes without touching existing code.

### 3. Liskov Substitution Principle

**Learning:** LSP ensures that derived classes honor the contracts of their base classes.

**Benefit:** Polymorphism works correctly. Any `CheckoutPolicy` implementation can be used interchangeably.

### 4. Interface Segregation Principle

**Learning:** Clients shouldn't be forced to depend on interfaces they don't use.

**Benefit:** Services have minimal, focused interfaces. A search client doesn't depend on checkout logic.

### 5. Dependency Inversion Principle

**Learning:** High-level modules should depend on abstractions, not concrete implementations.

**Benefit:** We can swap implementations (e.g., different notification services) without changing business logic.

### 6. Facade Pattern

**Learning:** The Facade pattern simplifies complex subsystems.

**Benefit:** `LibraryFacade` provides a clean API while hiding the complexity of coordinating multiple services.

---

## Challenges Encountered

### 1. Test Compatibility

**Challenge:** Existing tests expected `LibraryService` to handle everything directly.

**Solution:** Made `LibraryService` delegate to `LibraryFacade` while maintaining the same method signatures. This preserved backward compatibility.

### 2. Dependency Injection with Mockito

**Challenge:** Tests use `@InjectMocks` which doesn't automatically inject `LibraryFacade`.

**Solution:** Created `LibraryFacade` manually in `LibraryService` constructor, passing in the mocked repositories.

### 3. Strategy Selection

**Challenge:** Choosing between Template Method and Strategy patterns for policies.

**Solution:** Strategy pattern was better because:
- More flexible (can change behavior at runtime)
- Easier to test (each strategy is independent)
- Better aligns with OCP

### 4. Coverage Requirements

**Challenge:** Initial coverage was 64%, below the 80% requirement.

**Solution:** Added focused unit tests for policy and report classes, bringing coverage to 86%.

---

## Design Decisions and Trade-offs

### Decision 1: Facade vs Direct Service Usage

**Options:**
1. Delete `LibraryService`, use `LibraryFacade` directly
2. Keep `LibraryService` as delegator to `LibraryFacade`

**Choice:** Option 2 (Delegator)

**Reasoning:**
- ✅ Maintains backward compatibility with existing tests
- ✅ Professor can run original tests against our code
- ✅ Demonstrates both Facade and Adapter patterns
- ❌ Adds one extra layer (minimal cost)

### Decision 2: Strategy vs Template Method

**Options:**
1. Strategy Pattern for policies
2. Template Method Pattern for policies

**Choice:** Strategy Pattern

**Reasoning:**
- ✅ More flexible (composition over inheritance)
- ✅ Easier to test
- ✅ Policies are independent, no shared code
- ✅ Better for this use case (simple, stateless policies)

### Decision 3: Factory Location

**Options:**
1. Factories in policy package
2. Factories in service package
3. Factories as Spring Beans

**Choice:** Factories in policy package with static methods

**Reasoning:**
- ✅ Co-located with the strategies they create
- ✅ Simple selection logic doesn't need Spring
- ✅ Can still use Spring if needed later

### Decision 4: Notification Implementation

**Options:**
1. Real email sending (Spring Mail)
2. Console output (System.out.println)
3. Mock notification service

**Choice:** Console output (System.out.println)

**Reasoning:**
- ✅ Assignment focuses on SOLID principles, not email integration
- ✅ Demonstrates DIP with interface abstraction
- ✅ Easy to test
- ✅ Can be swapped for real implementation later

---

## Conclusion

This refactoring demonstrates how SOLID principles lead to:

1. **Better Code Organization** - Clear separation of concerns
2. **Improved Testability** - 86% coverage with focused unit tests
3. **Enhanced Maintainability** - Changes are localized
4. **Greater Extensibility** - New features don't require modifying existing code
5. **Reduced Coupling** - Services depend on abstractions, not concrete implementations

The most valuable lesson: **SOLID principles aren't academic concepts—they're practical guidelines that make code easier to work with.**

---

## Statistics

- **Classes/Interfaces Created:** 23
- **Test Classes Created:** 6
- **Total Test Methods:** 26
- **Code Coverage:** 86%
- **Lines Refactored:** ~500+
- **Time Investment:** ~8 hours
- **SOLID Violations Remaining:** 0

---

## Future Improvements

If we had more time, we could:

1. **Add Real Email Service** - Integrate Spring Mail for actual email sending
2. **Add Transaction Management** - Use `@Transactional` for atomic operations
3. **Create REST API** - Build controllers on top of `LibraryFacade`
4. **Add More Membership Types** - Demonstrate OCP by adding Faculty, Senior, etc.
5. **Implement Observer Pattern** - Notify when books become available
6. **Add Caching** - Cache frequently accessed data
7. **Add Validation** - Use Bean Validation more extensively
8. **Create DTOs** - Separate API models from entities

---

**End of Report**