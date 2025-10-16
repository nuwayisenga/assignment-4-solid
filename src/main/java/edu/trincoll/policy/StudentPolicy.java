package edu.trincoll.policy;
import edu.trincoll.model.Member;

public class StudentPolicy implements CheckoutPolicy {
    public int getMaxBooks() {
        return 5;
    }
    public int getLoanPeriodDays() {
        return 21;
    }
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}
