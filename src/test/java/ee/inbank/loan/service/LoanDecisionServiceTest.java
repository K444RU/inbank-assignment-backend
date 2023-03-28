package ee.inbank.loan.service;

import ee.inbank.loan.dto.LoanDecisionResponse;
import ee.inbank.loan.exception.LoanAmountOutOfBoundsException;
import ee.inbank.loan.exception.LoanPeriodOutOfBoundsException;
import ee.inbank.loan.service.LoanDecisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
public class LoanDecisionServiceTest {


    private LoanDecisionService loanDecisionService;

    @BeforeEach
    public void setUp() {
        this.loanDecisionService = new LoanDecisionService();
    }

    @Test

    public void calculate_whenAmountIsOutOfBounds_throwsException(){
        assertThrows(LoanAmountOutOfBoundsException.class,
                () -> loanDecisionService.calculate("49002010976", 1999, 12));

        assertThrows(LoanAmountOutOfBoundsException.class,
                () -> loanDecisionService.calculate("49002010976", 10001, 12));
    }

    @Test
    public void calculate_whenPeriodIsOutOfBounds_throwsException() {
        assertThrows(LoanPeriodOutOfBoundsException.class,
                () -> loanDecisionService.calculate("49002010976", 2000, 11));

        assertThrows(LoanPeriodOutOfBoundsException.class,
                () -> loanDecisionService.calculate("49002010976", 2000, 61));
    }

    @Test
    public void calculate_whenPersonHasDebt_returnsNegativeDecision() {
        LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate("49002010965", 2000, 12);

        assertFalse(loanDecisionResponse.isApproved());
        assertEquals("Negative, person has debt", loanDecisionResponse.getDecisionDescription());
    }


}
