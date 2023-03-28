package ee.inbank.loan.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoanDecisionResponse implements Serializable {

    private boolean isApproved;
    private int approvedAmount;
    private int approvedPeriod;
    private String decisionDescription;

    public LoanDecisionResponse(boolean isApproved, int approvedAmount, int approvedPeriod, String decisionDescription){
        this.isApproved = isApproved;
        this.approvedAmount = approvedAmount;
        this.approvedPeriod = approvedPeriod;
        this.decisionDescription = decisionDescription;
    }
}
