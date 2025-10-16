package edu.trincoll.policy;

import edu.trincoll.model.MembershipType;

public class CheckoutPolicyFactory {
    public static CheckoutPolicy getCheckoutPolicy(MembershipType type) {
        return switch (type) {
            case REGULAR -> new RegularPolicy();
            case PREMIUM -> new PremiumPolicy();
            case STUDENT -> new StudentPolicy();
            default -> throw new IllegalStateException("Unknown Membership Type");
        };
    }
}
