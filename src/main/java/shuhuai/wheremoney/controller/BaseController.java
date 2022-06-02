package shuhuai.wheremoney.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.service.excep.BaseException;
import shuhuai.wheremoney.service.excep.book.TitleOccupiedException;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.service.excep.common.TokenExpireException;
import shuhuai.wheremoney.service.excep.user.UserMissingException;
import shuhuai.wheremoney.service.excep.user.UserNameOccupiedException;
import shuhuai.wheremoney.service.excep.user.UserNamePasswordErrorException;

@Slf4j
public class BaseController {
    @ExceptionHandler(BaseException.class)
    public Response<Object> handleServiceException(BaseException error) {
        Response<Object> response = new Response<>();
        if (error instanceof UserNameOccupiedException || error instanceof TitleOccupiedException) {
            response.setCode(400);
        } else if (error instanceof UserNamePasswordErrorException || error instanceof TokenExpireException) {
            response.setCode(401);
        } else if (error instanceof ParamsException) {
            response.setCode(422);
        } else if (error instanceof ServerException) {
            response.setCode(500);
        } else if (error instanceof UserMissingException) {
            response.setCode(404);
        }
        response.setMessage(error.getMessage());
        return response;
    }
}