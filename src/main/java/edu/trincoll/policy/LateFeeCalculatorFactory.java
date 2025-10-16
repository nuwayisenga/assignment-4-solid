package edu.trincoll.policy;

import edu.trincoll.model.MembershipType;
import org.springframework.stereotype.Component;

@Component
public class LateFeeCalculatorFactory {

    /**
     * Gets the appropriate late fee calculator for a membership type.
     * @param membershipType The type of membership.
     * @return A late fee calculator instance for the given membership type.
     */
    public static LateFeeCalculator getLateFeeCalculator(MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> new RegularLateFeeCalculator();
            case PREMIUM -> new PremiumLateFeeCalculator();
            case STUDENT -> new StudentLateFeeCalculator();
        };
    }
}