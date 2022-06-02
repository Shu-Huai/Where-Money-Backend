package shuhuai.wheremoney.response.budget;

import shuhuai.wheremoney.entity.Budget;

import java.util.List;

public class GetAllBudgetResponse {
    private List<Budget> budgetList;

    public GetAllBudgetResponse(List<Budget> budgetList) {
        this.budgetList = budgetList;
    }

    public List<Budget> getBudgetList() {
        return budgetList;
    }

    public void setBudgetList(List<Budget> budgetList) {
        this.budgetList = budgetList;
    }
}
