package shuhuai.wheremoney.response;

import java.io.Serializable;

public class Response<Type> implements Serializable {
    private Integer code;
    private String message;
    private Type data;

    public Response() {
        code = 0;
        message = "";
        data = null;
    }

    public Response(Integer code) {
        this.code = code;
    }

    public Response(Throwable error) {
        this.message = error.getMessage();
    }

    public Response(Integer code, Type data) {
        this.code = code;
        this.data = data;
    }

    public Response(Integer code, String message, Type data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getData() {
        return data;
    }

    public void setData(Type data) {
        this.data = data;
    }
}
