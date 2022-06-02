package shuhuai.wheremoney.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.response.user.ChangeUserNameResponse;
import shuhuai.wheremoney.response.user.LoginResponse;
import shuhuai.wheremoney.service.UserService;
import shuhuai.wheremoney.utils.TokenValidator;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
@Slf4j
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private TokenValidator tokenValidator;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "注册")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "注册成功"),
            @ApiResponse(code = 400, message = "用户名已被占用"),
            @ApiResponse(code = 422, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器错误")
    })
    public Response<Object> register(@RequestParam String userName, @RequestParam String password) {
        userService.register(userName, password);
        return new Response<>(200, "注册成功", null);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登录")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "登录成功"),
            @ApiResponse(code = 401, message = "账户或密码错误"),
            @ApiResponse(code = 422, message = "参数错误"),
    })
    public Response<LoginResponse> login(@RequestParam String userName, @RequestParam String password) {
        userService.login(userName, password);
        String token = tokenValidator.getToken(userName);
        return new Response<>(200, "登录成功", new LoginResponse(token));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "修改用户名成功"),
            @ApiResponse(code = 400, message = "用户名已被占用"),
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器错误")
    })
    @RequestMapping(value = "/change-user-name", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户名")
    public Response<ChangeUserNameResponse> changeUserName(@RequestParam String userName) {
        String oldUserName = TokenValidator.getUser().get("userName");
        userService.changeUsername(oldUserName, userName);
        String token = tokenValidator.getToken(userName);
        return new Response<>(200, "修改用户名成功", new ChangeUserNameResponse(token));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "修改用户名成功"),
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 422, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器错误")
    })
    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Response<Object> changePassword(@RequestParam String password) {
        String userName = TokenValidator.getUser().get("userName");
        userService.changePassword(userName, password);
        return new Response<>(200, "修改密码成功", null);
    }
}