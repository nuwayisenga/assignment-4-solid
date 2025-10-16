package edu.trincoll.policy;

public class PremiumLateFeeCalculator implements LateFeeCalculator {
    @Override
    public double calculateLateFee(long daysLate) {
        return 0.0; // Premium members don't pay late fees
    }
}