package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.Member;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
/**
 * Email-based implementation of the notification service.
 * Sends notifications to members via email for checkout and return operations.
 */
@Service
public class EmailNotificationService implements NotificationService {
    /**
     * Sends a checkout notification email to a member.
     * @param member The member who checked out the book.
     * @param book The book that was checked out.
     * @param dueDate The date the book is due for return.
     */
    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book checked out");
        System.out.println("Message: You have checked out " + book.getTitle());
    }
    /**
     * Sends a return notification email to a member.
     * @param member The member who returned the book.
     * @param book The book that was returned.
     * @param lateFee The late fee amount, or 0.0 if returned on time.
     */
    @Override
    public void sendReturnNotification(Member member, Book book, double lateFee) {
        System.out.println("Sending email to: " + member.getEmail());
        System.out.println("Subject: Book returned");
        System.out.println("Message: You have returned " + book.getTitle());
    }
}
