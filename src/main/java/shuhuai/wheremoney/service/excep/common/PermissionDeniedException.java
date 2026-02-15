package shuhuai.wheremoney.service.excep.common;

import shuhuai.wheremoney.service.excep.BaseException;

public class PermissionDeniedException extends BaseException {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
