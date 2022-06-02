package shuhuai.wheremoney.service.excep.common;

import shuhuai.wheremoney.service.excep.BaseException;

public class TokenExpireException extends BaseException {
    public TokenExpireException(String message) {
        super(message);
    }
}
