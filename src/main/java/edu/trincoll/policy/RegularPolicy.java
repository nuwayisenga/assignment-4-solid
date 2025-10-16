package edu.trincoll.policy;

import edu.trincoll.model.Member;

public class RegularPolicy implements CheckoutPolicy {
    public int getMaxBooks() {
        return 3;
    }
    public int getLoanPeriodDays() {
        return 14;
    }
    public boolean canCheckout(Member member) {
        return member.getBooksCheckedOut() < getMaxBooks();
    }
}
