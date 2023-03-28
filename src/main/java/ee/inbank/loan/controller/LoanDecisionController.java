package ee.inbank.loan.controller;

import ee.inbank.loan.dto.LoanDecisionResponse;
import ee.inbank.loan.exception.LoanAmountOutOfBoundsException;
import ee.inbank.loan.exception.LoanPeriodOutOfBoundsException;
import ee.inbank.loan.service.LoanDecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoanDecisionController {

    private static final Logger logger = LoggerFactory.getLogger(LoanDecisionController.class);

    private final LoanDecisionService loanDecisionService;

    @Autowired
    public LoanDecisionController(LoanDecisionService loanDecisionService) {
        this.loanDecisionService = loanDecisionService;
    }

    @CrossOrigin
    @GetMapping("/loan/decision")
    public ResponseEntity<?> getLoanDecision(@RequestParam String personalCode,
                                             @RequestParam int loanAmount,
                                             @RequestParam int loanPeriod) {
        try {
            logger.info("[REQUEST]: Loan decision requested for (personalCode={}, loanAmount={}, loanPeriod={})",
                    personalCode, loanAmount, loanPeriod);
            LoanDecisionResponse loanDecisionResponse = loanDecisionService.calculate(personalCode, loanAmount, loanPeriod);
            return new ResponseEntity<>(loanDecisionResponse, HttpStatus.OK);
        } catch (LoanAmountOutOfBoundsException | LoanPeriodOutOfBoundsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
}
