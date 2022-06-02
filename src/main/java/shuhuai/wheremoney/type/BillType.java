package shuhuai.wheremoney.type;

public enum BillType {
    收入("收入"), 支出("支出"), 转账("转账"), 退款("退款");

    private String type;

    BillType(String type) {
        this.type = type;
    }

    public static BillType getBillTypeEnum(String type) {
        return switch (type) {
            case "收入" -> 收入;
            case "支出" -> 支出;
            case "转账" -> 转账;
            case "退款" -> 退款;
            default -> null;
        };
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}