package shuhuai.wheremoney.service.excep.user;

import shuhuai.wheremoney.service.excep.BaseException;

/*** 账号重复异常
 * @author 殊怀丶
 * @version 1.0
 */
public class UserNameOccupiedException extends BaseException {
    public UserNameOccupiedException(String message) {
        super(message);
    }
}