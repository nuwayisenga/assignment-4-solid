package edu.trincoll.policy;

public interface LateFeeCalculator {
    double calculateLateFee(long daysLate);
}