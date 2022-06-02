package shuhuai.wheremoney.response.user;

public class ChangeUserNameResponse {
    private String token;

    public ChangeUserNameResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

