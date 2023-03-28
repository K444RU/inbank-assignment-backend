package ee.inbank.loan.service;

import ee.inbank.loan.dto.LoanDecisionResponse;
import ee.inbank.loan.exception.LoanAmountOutOfBoundsException;
import ee.inbank.loan.exception.LoanPeriodOutOfBoundsException;
import ee.inbank.loan.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoanDecisionService {
    private static final Logger logger = LoggerFactory.getLogger(LoanDecisionService.class);
    private static final int MIN_LOAN_AMOUNT = 2000;
    private static final int MAX_LOAN_AMOUNT = 10000;
    private static final int MAX_LOAN_PERIOD = 60;
    private static final int MIN_LOAN_PERIOD = 12;
    private static final Map<String, Integer> CREDIT_MODIFIERS = new HashMap<>();

    static {
        CREDIT_MODIFIERS.put("49002010965", 0); // debt
        CREDIT_MODIFIERS.put("49002010976", 100); // segment 1
        CREDIT_MODIFIERS.put("49002010987", 300); // segment 2
        CREDIT_MODIFIERS.put("49002010998", 1000); // segment 3
    }

    public LoanDecisionResponse calculate(String personalCode, int loanAmount, int loanPeriod)
            throws LoanAmountOutOfBoundsException, LoanPeriodOutOfBoundsException {
        if (!isAmountInBounds(loanAmount)) {
            logger.error("Loan amount (loanAmount={}) is out of bounds (2000-10000)", loanAmount);
            throw new LoanAmountOutOfBoundsException();
        } else if (!isPeriodInBounds(loanPeriod)) {
            logger.error("Loan period (loanPeriod={}) is out of bounds (12-60)", loanPeriod);
            throw new LoanPeriodOutOfBoundsException();
        }

        Loan loan = new Loan(personalCode, loanAmount, loanPeriod);

        int creditModifier = CREDIT_MODIFIERS.getOrDefault(personalCode, 0);
        loan.setCreditModifier(creditModifier);

        if (loan.getCreditModifier() == 0) {
            return new LoanDecisionResponse(false, 0, 0, "Negative, person has debt");
        } else {
            double creditScore = calculateCreditScore(loan.getCreditModifier(), loan.getLoanAmount(), loan.getLoanPeriod());
            loan.setCreditScore(creditScore);
            logger.info("Credit score is {}", creditScore);

            adjustLoanAmount(loan);

            if (loan.getApprovedLoanAmount() < MIN_LOAN_AMOUNT) {
                logger.info("Suitable loan amount within selected period was not found");
                findSuitableLoanPeriod(loan);
            }

            if (loan.getApprovedLoanAmount() < MIN_LOAN_AMOUNT) {
                logger.info("Negative decision. No suitable loan amount found within any period.");
                return new LoanDecisionResponse(false, 0, 0, "Negative");
            } else {
                logger.info("Positive decision. Approved loan is {} € for {} months.", loan.getApprovedLoanAmount(), loan.getLoanPeriod());
                return new LoanDecisionResponse(true, loan.getApprovedLoanAmount(), loan.getLoanPeriod(), "Positive");
            }
        }
    }

    /**
     * If credit score is higher than 1.0, a larger sum could be approved, adjusts loan amount until finds maximum sum.
     * If credit score is lower than 1.0, adjusts loan amount to the largest possible sum.
     * If credit score is equal to 1.0, keeps loan amount unchanged.
     *
     * @param loan object, containing requested loan amount, period and credit modifier.
     */
    private void adjustLoanAmount(Loan loan) {
        int adjustedLoanAmount = loan.getLoanAmount();
        double adjustedCreditScore = loan.getCreditScore();
        if (loan.getCreditScore() > 1.0) {
            logger.info("A larger sum could be approved");
            while (adjustedCreditScore > 1.0 && adjustedLoanAmount < MAX_LOAN_AMOUNT) {
                adjustedLoanAmount += 100;
                adjustedCreditScore = calculateCreditScore(loan.getCreditModifier(), adjustedLoanAmount, loan.getLoanPeriod());
            }
        } else if (loan.getCreditScore() < 1.0) {
            logger.info("Such sum could not be approved");
            while (adjustedCreditScore < 1.0 && adjustedLoanAmount > MIN_LOAN_AMOUNT) {
                adjustedLoanAmount -= 100;
                adjustedCreditScore = calculateCreditScore(loan.getCreditModifier(), adjustedLoanAmount, loan.getLoanPeriod());
            }
        }
        loan.setLoanAmount(adjustedLoanAmount);
        loan.setCreditScore(adjustedCreditScore);
        int approvedLoanAmount = (int) (Math.min(adjustedCreditScore, 1.0) * adjustedLoanAmount);
        loan.setApprovedLoanAmount(approvedLoanAmount);

        logger.info("Largest sum to approve {}", approvedLoanAmount);
    }

    /**
     * If suitable loan amount within selected period was not found,
     * finds a new suitable loan period for requested loan amount.
     *
     * @param loan object, containing requested loan amount, period and credit modifier.
     */
    private void findSuitableLoanPeriod(Loan loan) {
        for (int i = loan.getLoanPeriod(); i < MAX_LOAN_PERIOD; i++) {
            double creditScore = calculatedCreditScore(loan.getCreditModifier(), loan.getLoanAmount(), i);
            if (creditScore >= 1) {
                logger.info("Loan {} € could be approved within new loan period {} months", loan.getLoanAmount(), i);
                loan.setLoanPeriod(i);
                loan.setApprovedLoanAmount(loan.getLoanAmount());
                break;
            }
        }
    }

    private boolean isPeriodInBounds(int loanPeriod) {
        return loanPeriod >= MIN_LOAN_PERIOD && loanPeriod <= MAX_LOAN_PERIOD;
    }

    private boolean isAmountInBounds(int loanAmount) {
        return loanAmount >= MIN_LOAN_AMOUNT && loanAmount <= MAX_LOAN_AMOUNT;
    }

    private double calculateCreditScore(int creditModifier, int loanAmount, int loanPeriod) {
        return (double) creditModifier / loanAmount * loanPeriod;
    }


    private double calculatedCreditScore(int creditModifier, int loanAmount, int loanPeriod) {
        return (double) creditModifier / loanAmount * loanPeriod;
    }
}
