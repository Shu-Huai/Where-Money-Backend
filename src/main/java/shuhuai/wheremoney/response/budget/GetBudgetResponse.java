package shuhuai.wheremoney.response.budget;

import shuhuai.wheremoney.entity.Budget;

public class GetBudgetResponse {
    Budget budget;

    public GetBudgetResponse(Budget budget) {
        this.budget = budget;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
}
