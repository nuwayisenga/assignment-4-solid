package edu.trincoll.service;

import edu.trincoll.model.Book;
import edu.trincoll.model.Member;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public void sendCheckoutNotification(Member member, Book book, LocalDate dueDate) {
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
