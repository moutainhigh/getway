package com.ebay.getway.handle;


import com.superarmyknife.toolbox.Exception.SAKException;
import com.superarmyknife.toolbox.constant.GlobleErrorCodeEnum;
import com.superarmyknife.toolbox.handle.ResponseCodeConstants;
import com.superarmyknife.toolbox.handle.ResponseData;
import com.superarmyknife.toolbox.handle.ResponseErrorMessageContext;
import com.superarmyknife.toolbox.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @project: 
 * @package: com..core.handler
 * @class: GlobalExceptionHandler
 * @description: controller层异常处理全局类
 * @author: www.superarmyknife.com
 * @date: 2018/11/11
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @name: missingServletRequestParameterExceptionHandler
     * @description: 增加对必填参数缺失的异常处理器
     * @param: [e]
     * @return: org.springframework.http.ResponseEntity<com..core.base.ResponseData>
     * @throws:
     * @author: www.superarmyknife.com
     * @create: 2018/11/11
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    private ResponseEntity<ResponseData> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("请求失败！参数缺失！", e);
        return ResponseEntity.ok(ResponseData.failure(ResponseCodeConstants.MISSING_REQUIRED_PARAMETER, "操作失败！参数缺失！"));
    }


    /**
     * @name: sakExceptionHandler
     * @description: 接收处理自定义SAKException
     * @param: [e]
     * @return: org.springframework.http.ResponseEntity<com..core.base.ResponseData>
     * @throws:
     * @author: www.superarmyknife.com
     * @create: 2018/11/11
     */
    @ExceptionHandler(SAKException.class)
    private ResponseEntity<ResponseData> sakExceptionHandler(SAKException e) {
        if (StringUtil.isNotBlank(ResponseErrorMessageContext.getMessage())) {
            String message = ResponseErrorMessageContext.getMessage() + StringUtil.nullToEmpty(ResponseErrorMessageContext.getReason());
            return ResponseEntity.ok(ResponseData.failure(ResponseErrorMessageContext.getCode(), message));
        }
        return ResponseEntity.ok(ResponseData.failure(e.getErrorCode(), e.getMessage()));
    }

    /**
     * @name: illegalArgumentExceptionHandler
     * @description: 接收处理参数非法异常IllegalArgumentException
     * @param: [e]
     * @return: org.springframework.http.ResponseEntity<com..core.base.ResponseData>
     * @throws:
     * @author: www.superarmyknife.com
     * @create: 2018/11/11
     */
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ResponseData> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("请求异常！请及时检查！", e);
        return ResponseEntity.ok(ResponseData.failure(GlobleErrorCodeEnum.ILLEGAL_PARAMS.getMsg()));
    }

    /**
     * @name: httpRequestMethodNotSupportedExceptionHandler
     * @description: 请求方式有误异常处理
     * @param: [e]
     * @return: org.springframework.http.ResponseEntity<com..core.base.ResponseData>
     * @throws:
     * @author: www.superarmyknife.com
     * @create: 2018/11/11
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ResponseData> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("请求方式异常！请及时检查！", e);
        return ResponseEntity.ok(ResponseData.failure(GlobleErrorCodeEnum.METHOHD_ERROR.getCode(), GlobleErrorCodeEnum.METHOHD_ERROR.getMsg()));
    }

    /**
     * @name: runtimeExceptionHandler
     * @description: 接收处理运行时全局异常，以防上面两个异常获取不到
     * @param: [e]
     * @return: org.springframework.http.ResponseEntity<com..core.base.ResponseData>
     * @throws:
     * @author: www.superarmyknife.com
     * @create: 2018/11/11
     */
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ResponseData> runtimeExceptionHandler(Exception e) {
        log.error("运行异常！请及时检查！", e);
        return ResponseEntity.ok(ResponseData.failure(GlobleErrorCodeEnum.SERVER_ERROR.getMsg()));
    }

}
