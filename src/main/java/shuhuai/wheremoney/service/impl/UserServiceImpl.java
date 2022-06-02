package shuhuai.wheremoney.service.impl;

import org.springframework.stereotype.Service;
import shuhuai.wheremoney.entity.Book;
import shuhuai.wheremoney.entity.User;
import shuhuai.wheremoney.mapper.BookMapper;
import shuhuai.wheremoney.mapper.UserMapper;
import shuhuai.wheremoney.service.BillCategoryService;
import shuhuai.wheremoney.service.UserService;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.service.excep.user.UserMissingException;
import shuhuai.wheremoney.service.excep.user.UserNameOccupiedException;
import shuhuai.wheremoney.service.excep.user.UserNamePasswordErrorException;
import shuhuai.wheremoney.utils.HashComputer;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private BillCategoryService billCategoryService;

    public void register(String userName, String password) throws ServerException, UserNameOccupiedException, ParamsException {
        if (userName == null || password == null) {
            throw new ParamsException("参数错误");
        }
        User sameName = userMapper.selectUserByUserName(userName);
        if (sameName != null) {
            throw new UserNameOccupiedException("用户名已被占用");
        }
        String hashedPassword = HashComputer.getHashedString(password);
        User user = new User(userName, hashedPassword);
        Integer result = userMapper.insertUserSelective(user);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        Book book = new Book(user.getId(), "默认账本", 1);
        result = bookMapper.insertBookSelective(book);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
        billCategoryService.addDefaultBillCategory(book.getId());
    }

    @Override
    public void login(String userName, String password) throws UserNamePasswordErrorException, ParamsException {
        if (userName == null || password == null) {
            throw new ParamsException("参数错误");
        }
        User result = userMapper.selectUserByUserName(userName);
        String hashedPassword = HashComputer.getHashedString(password);
        if (result == null || !result.getHashedPassword().equals(hashedPassword)) {
            throw new UserNamePasswordErrorException("账户或密码错误");
        }
    }

    @Override
    public void changeUsername(String oldUserName, String userName) throws UserNameOccupiedException, UserMissingException, ParamsException, ServerException {
        if (oldUserName == null || userName == null) {
            throw new ParamsException("参数错误");
        }
        User user = userMapper.selectUserByUserName(oldUserName);
        if (user == null) {
            throw new UserMissingException("用户不存在");
        }
        if (userMapper.selectUserByUserName(userName) != null) {
            throw new UserNameOccupiedException("用户名已被占用");
        }
        user.setUserName(userName);
        Integer result = userMapper.updateUserSelectiveById(user);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
    }

    @Override
    public void changePassword(String userName, String password) throws ParamsException, ServerException, UserMissingException {
        if (userName == null || password == null) {
            throw new ParamsException("参数错误");
        }
        User user = userMapper.selectUserByUserName(userName);
        if (user == null) {
            throw new UserMissingException("用户不存在");
        }
        user.setHashedPassword(HashComputer.getHashedString(password));
        Integer result = userMapper.updateUserSelectiveById(user);
        if (result != 1) {
            throw new ServerException("服务器错误");
        }
    }
}