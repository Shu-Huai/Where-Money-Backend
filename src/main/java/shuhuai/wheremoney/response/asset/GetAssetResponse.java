package shuhuai.wheremoney.response.asset;

import shuhuai.wheremoney.entity.Asset;

public class GetAssetResponse {
    private Asset asset;

    public GetAssetResponse(Asset asset) {
        this.asset = asset;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}
