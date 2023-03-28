package ee.inbank.loan.service;

import ee.inbank.loan.dto.LoanDecisionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class LoanDecisionServiceSegmentTwoTest {

    private LoanDecisionService loanDecisionService;

    @BeforeEach
    public void setUp() {
        this.loanDecisionService = new LoanDecisionService();
    }

    @Test
    public void calculate_whenAmountAndPeriodSuitable_returnsPositiveDecision() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010987", 3600, 12);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(3600, loanDecisionResponse.getApprovedAmount());
        assertEquals(12, loanDecisionResponse.getApprovedPeriod());
    }

    @Test
    public void calculate_whenLesserAmountFound_returnsPositiveDecisionAndNewAmount() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010987", 6000, 12);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(3600, loanDecisionResponse.getApprovedAmount());
        assertEquals(12, loanDecisionResponse.getApprovedPeriod());
    }

    @Test
    public void calculate_whenGreaterAmountFound_returnsPositiveDecisionAndNewAmount() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010987", 3600, 20);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(6000, loanDecisionResponse.getApprovedAmount());
        assertEquals(20, loanDecisionResponse.getApprovedPeriod());
    }
}
