package shuhuai.wheremoney.type;

public enum AssetType {
    资金("资金"), 信用卡("信用卡"), 充值("充值"), 投资理财("投资理财");
    private String type;

    AssetType(String type) {
        this.type = type;
    }

    public static AssetType getAssetTypeEnum(String type) {
        return switch (type) {
            case "资金" -> 资金;
            case "信用卡" -> 信用卡;
            case "充值" -> 充值;
            case "投资理财" -> 投资理财;
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