package ee.inbank.loan.service;

import ee.inbank.loan.dto.LoanDecisionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class LoanDecisionServiceSegmentThreeTest {

    private LoanDecisionService loanDecisionService;

    @BeforeEach
    public void setUp(){this.loanDecisionService = new LoanDecisionService();}

    @Test
    public void calculate_whenAmountAndPeriodSuitable_returnsPositiveDecision(){
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010998", 10000, 12);

        assertTrue(loanDecisionResponse.isApproved());
        assertEquals(10000, loanDecisionResponse.getApprovedAmount());
        assertEquals(12, loanDecisionResponse.getApprovedPeriod());
    }
}
