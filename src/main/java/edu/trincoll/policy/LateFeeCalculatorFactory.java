package edu.trincoll.policy;

import edu.trincoll.model.MembershipType;
import org.springframework.stereotype.Component;

@Component
public class LateFeeCalculatorFactory {

    public static LateFeeCalculator getLateFeeCalculator(MembershipType membershipType) {
        return switch (membershipType) {
            case REGULAR -> new RegularLateFeeCalculator();
            case PREMIUM -> new PremiumLateFeeCalculator();
            case STUDENT -> new StudentLateFeeCalculator();
        };
    }
}