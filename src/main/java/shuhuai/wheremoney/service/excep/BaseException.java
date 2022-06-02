package shuhuai.wheremoney.service.excep;

/*** 业务层异常类基类
 * @author 殊怀丶
 * @version 1.0
 */
public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }
}