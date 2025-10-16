package edu.trincoll.policy;

public class RegularLateFeeCalculator implements LateFeeCalculator {
    private static final double DAILY_FEE = 0.50;

    @Override
    public double calculateLateFee(long daysLate) {
        return daysLate * DAILY_FEE;
    }
}