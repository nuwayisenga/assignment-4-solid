package edu.trincoll.policy;

import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Checkout Policy Tests")
class CheckoutPolicyTest {

    @Test
    @DisplayName("Regular policy should have correct limits")
    void regularPolicyShouldHaveCorrectLimits() {
        CheckoutPolicy policy = new RegularPolicy();

        assertThat(policy.getMaxBooks()).isEqualTo(3);
        assertThat(policy.getLoanPeriodDays()).isEqualTo(14);
    }

    @Test
    @DisplayName("Premium policy should have correct limits")
    void premiumPolicyShouldHaveCorrectLimits() {
        CheckoutPolicy policy = new PremiumPolicy();

        assertThat(policy.getMaxBooks()).isEqualTo(10);
        assertThat(policy.getLoanPeriodDays()).isEqualTo(30);
    }

    @Test
    @DisplayName("Student policy should have correct limits")
    void studentPolicyShouldHaveCorrectLimits() {
        CheckoutPolicy policy = new StudentPolicy();

        assertThat(policy.getMaxBooks()).isEqualTo(5);
        assertThat(policy.getLoanPeriodDays()).isEqualTo(21);
    }

    @Test
    @DisplayName("Factory should return correct policy for membership type")
    void factoryShouldReturnCorrectPolicy() {
        CheckoutPolicy regularPolicy = CheckoutPolicyFactory.getCheckoutPolicy(MembershipType.REGULAR);
        CheckoutPolicy premiumPolicy = CheckoutPolicyFactory.getCheckoutPolicy(MembershipType.PREMIUM);
        CheckoutPolicy studentPolicy = CheckoutPolicyFactory.getCheckoutPolicy(MembershipType.STUDENT);

        assertThat(regularPolicy).isInstanceOf(RegularPolicy.class);
        assertThat(premiumPolicy).isInstanceOf(PremiumPolicy.class);
        assertThat(studentPolicy).isInstanceOf(StudentPolicy.class);
    }

    @Test
    @DisplayName("Should allow checkout when under limit")
    void shouldAllowCheckoutWhenUnderLimit() {
        Member member = new Member("Test", "test@example.com");
        member.setBooksCheckedOut(2);

        CheckoutPolicy policy = new RegularPolicy();

        assertThat(policy.canCheckout(member)).isTrue();
    }

    @Test
    @DisplayName("Should not allow checkout when at limit")
    void shouldNotAllowCheckoutWhenAtLimit() {
        Member member = new Member("Test", "test@example.com");
        member.setBooksCheckedOut(3);

        CheckoutPolicy policy = new RegularPolicy();

        assertThat(policy.canCheckout(member)).isFalse();
    }
}