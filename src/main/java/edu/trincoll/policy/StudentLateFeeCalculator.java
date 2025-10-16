package edu.trincoll.policy;

public class StudentLateFeeCalculator implements LateFeeCalculator {
    private static final double DAILY_FEE = 0.25;

    @Override
    public double calculateLateFee(long daysLate) {
        return daysLate * DAILY_FEE;
    }
}