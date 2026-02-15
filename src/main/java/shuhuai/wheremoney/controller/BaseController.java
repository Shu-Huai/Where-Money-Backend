package shuhuai.wheremoney.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.mybatis.spring.MyBatisSystemException;
import shuhuai.wheremoney.response.Response;
import shuhuai.wheremoney.service.excep.BaseException;
import shuhuai.wheremoney.service.excep.book.TitleOccupiedException;
import shuhuai.wheremoney.service.excep.common.ParamsException;
import shuhuai.wheremoney.service.excep.common.PermissionDeniedException;
import shuhuai.wheremoney.service.excep.common.ServerException;
import shuhuai.wheremoney.service.excep.common.TokenExpireException;
import shuhuai.wheremoney.service.excep.user.UserMissingException;
import shuhuai.wheremoney.service.excep.user.UserNameOccupiedException;
import shuhuai.wheremoney.service.excep.user.UserNamePasswordErrorException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

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
        } else if (error instanceof PermissionDeniedException) {
            response.setCode(403);
        }
        response.setMessage(error.getMessage());
        return response;
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            BindException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class
    })
    public Response<Object> handleSpringParamsException(Exception error) {
        return new Response<>(422, "参数错误", error.getMessage());
    }

    @ExceptionHandler({
            MyBatisSystemException.class,
            PersistenceException.class,
            BindingException.class,
            DataAccessException.class,
            SQLException.class,
            DuplicateKeyException.class
    })
    public Response<Object> handleDatabaseException(Exception error) {
        log.error("数据库/MyBatis异常", error);
        if (error instanceof DataIntegrityViolationException
                || error instanceof SQLIntegrityConstraintViolationException) {
            return new Response<>(422, "参数错误", error.getMessage());
        }
        return new Response<>(500, "数据库错误", null);
    }

    @ExceptionHandler(Exception.class)
    public Response<Object> handleException(Exception error) {
        log.error("未处理异常", error);
        return new Response<>(500, "服务器错误", null);
    }
}
