package com.ebay.getway.controller;

import com.superarmyknife.toolbox.handle.ResponseData;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class ErrorControllerHandel  extends AbstractErrorController {


    public ErrorControllerHandel(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    /**
         * 出异常后进入该方法，交由下面的方法处理
         */
        @Override
        public String getErrorPath() {
            return "/error";
        }

    /**
     *   private ResponseEntity<ResponseData> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
     *         log.error("请求失败！参数缺失！", e);
     *         return ResponseEntity.ok(ResponseData.failure(ResponseCodeConstants.MISSING_REQUIRED_PARAMETER, "操作失败！参数缺失！"));
     *     }
     * @return
     */
    @RequestMapping("/error")
    @ResponseBody
    public ResponseData error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                true);
        ;
        HttpStatus status = getStatus(request);
        return ResponseData.failure(String.valueOf(status.value()),body.get("message").toString());
    }


}
