package shuhuai.wheremoney.service.excep.user;

import shuhuai.wheremoney.service.excep.BaseException;

public class UserNamePasswordErrorException extends BaseException {
    public UserNamePasswordErrorException(String message) {
        super(message);
    }
}