package shuhuai.wheremoney.service.excep.book;

import shuhuai.wheremoney.service.excep.BaseException;

/*** 标题重复异常
 * @author 殊怀丶
 * @version 1.0
 */
public class TitleOccupiedException extends BaseException {
    public TitleOccupiedException(String message) {
        super(message);
    }
}