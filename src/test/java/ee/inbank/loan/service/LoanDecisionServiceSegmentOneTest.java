package ee.inbank.loan.service;

import ee.inbank.loan.dto.LoanDecisionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
public class LoanDecisionServiceSegmentOneTest {

    private LoanDecisionService loanDecisionService;

    @BeforeEach
    public void setUp() {
        this.loanDecisionService = new LoanDecisionService();
    }

    @Test
    public void calculate_whenSuitablePeriodFound_returnsPositiveDecisionAndNewPeriod() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010976", 2000, 12);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(2000, loanDecisionResponse.getApprovedAmount());
        assertEquals(20, loanDecisionResponse.getApprovedPeriod());
    }

    @Test
    public void calculate_whenAmountAndPeriodSuitable_returnsPositiveDecision() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010976", 2000, 20);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(2000, loanDecisionResponse.getApprovedAmount());
        assertEquals(20, loanDecisionResponse.getApprovedPeriod());
    }

    @Test
    public void calculate_whenLesserAmountFound_returnsPositiveDecisionAndNewAmount() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010976", 3000, 20);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(2000, loanDecisionResponse.getApprovedAmount());
        assertEquals(20, loanDecisionResponse.getApprovedPeriod());
    }

    @Test
    public void calculate_whenGreaterAmountFound_returnsPositiveDecisionAndNewAmount() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010976", 3000, 40);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(4000, loanDecisionResponse.getApprovedAmount());
        assertEquals(40, loanDecisionResponse.getApprovedPeriod());
    }

}
