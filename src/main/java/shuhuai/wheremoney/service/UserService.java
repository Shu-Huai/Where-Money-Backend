package shuhuai.wheremoney.service;

import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.service.excep.user.UserMissingException;
import shuhuai.wheremoney.service.excep.user.UserNameOccupiedException;
import shuhuai.wheremoney.service.excep.user.UserNamePasswordErrorException;

public interface UserService {
    void register(String userName, String password) throws ServerException, UserNameOccupiedException, ParamsException;

    void login(String userName, String password) throws UserNamePasswordErrorException, ParamsException;

    void changeUsername(String oldUserName, String userName) throws UserNameOccupiedException, UserMissingException, ParamsException, ServerException;

    void changePassword(String userName, String password) throws ParamsException, ServerException, UserMissingException;
}