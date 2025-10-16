package edu.trincoll.policy;

import edu.trincoll.model.Member;

public class PremiumPolicy implements CheckoutPolicy {
    public int getMaxBooks() {
        return 10;
    }
    public int getLoanPeriodDays() {
        return 30;
    }
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}
