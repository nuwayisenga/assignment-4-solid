package edu.trincoll.policy;

import edu.trincoll.model.MembershipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Late Fee Calculator Tests")
class LateFeeCalculatorTest {

    @Test
    @DisplayName("Regular calculator should charge $0.50 per day")
    void regularCalculatorShouldChargeCorrectly() {
        LateFeeCalculator calculator = new RegularLateFeeCalculator();

        assertThat(calculator.calculateLateFee(5)).isEqualTo(2.50);
        assertThat(calculator.calculateLateFee(10)).isEqualTo(5.00);
    }

    @Test
    @DisplayName("Premium calculator should charge nothing")
    void premiumCalculatorShouldChargeNothing() {
        LateFeeCalculator calculator = new PremiumLateFeeCalculator();

        assertThat(calculator.calculateLateFee(5)).isEqualTo(0.0);
        assertThat(calculator.calculateLateFee(100)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Student calculator should charge $0.25 per day")
    void studentCalculatorShouldChargeCorrectly() {
        LateFeeCalculator calculator = new StudentLateFeeCalculator();

        assertThat(calculator.calculateLateFee(5)).isEqualTo(1.25);
        assertThat(calculator.calculateLateFee(10)).isEqualTo(2.50);
    }

    @Test
    @DisplayName("Factory should return correct calculator for membership type")
    void factoryShouldReturnCorrectCalculator() {
        LateFeeCalculator regularCalc = LateFeeCalculatorFactory.getLateFeeCalculator(MembershipType.REGULAR);
        LateFeeCalculator premiumCalc = LateFeeCalculatorFactory.getLateFeeCalculator(MembershipType.PREMIUM);
        LateFeeCalculator studentCalc = LateFeeCalculatorFactory.getLateFeeCalculator(MembershipType.STUDENT);

        assertThat(regularCalc).isInstanceOf(RegularLateFeeCalculator.class);
        assertThat(premiumCalc).isInstanceOf(PremiumLateFeeCalculator.class);
        assertThat(studentCalc).isInstanceOf(StudentLateFeeCalculator.class);
    }
}