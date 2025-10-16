package edu.trincoll.policy;

import edu.trincoll.model.MembershipType;
import org.springframework.stereotype.Component;

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