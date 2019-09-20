package com.yoloho.enhanced.spring.support;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.alibaba.fastjson.JSON;
import com.yoloho.enhanced.common.support.MsgBean;
import com.yoloho.enhanced.spring.exception.InvokingException;
import com.yoloho.enhanced.spring.util.PropertyUtil;

/**
 * 默认的未捕获异常处理器，会在json请求中返回标准的json错误
 * 
 * @author jason
 *
 */
public class CustomExceptionHandler extends AbstractHandlerExceptionResolver {
    private static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class.getSimpleName());
    
    /**
     * 是否处理非json请求的异常拦截(直接输出错误内容)
     */
    private static boolean handleNormalRequest = false;
    
    /**
     * 将所以请求均当作json请求
     */
    private static boolean takeAllRequestAsJSON = false;
    
    private static String normalMsgError = null;
    private static String jsonMsgError = null;
    
    public CustomExceptionHandler() {
        super();
        setOrder(-1);
        logger.info("init default exception handler");
    }
    
    @PostConstruct
    public void init() {
        String resolved = null;
        if (StringUtils.isNotEmpty(jsonMsgError) && jsonMsgError.startsWith("${")) {
            // property resolving
            resolved = PropertyUtil.resolveProperty(jsonMsgError);
            if (StringUtils.equals(jsonMsgError, resolved)) {
                // fail
                jsonMsgError = null;
            } else {
                jsonMsgError = resolved;
            }
        }
        if (StringUtils.isNotEmpty(normalMsgError) && normalMsgError.startsWith("${")) {
            // property resolving
            resolved = PropertyUtil.resolveProperty(normalMsgError);
            if (StringUtils.equals(normalMsgError, resolved)) {
                // fail
                normalMsgError = null;
            } else {
                normalMsgError = resolved;
            }
        }
    }
    
    @Override
    @ResponseBody
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception exception) {
        MsgBean msg = new MsgBean();
        boolean isDetailErrorMsg = false;
        if (exception instanceof TypeMismatchException) {// wuzl类型不正确
            TypeMismatchException typeEx = (TypeMismatchException) exception;
            msg.failure(1, String.format("Param [%s] is in wrong format", typeEx.getValue()));
        } else if (exception instanceof MissingServletRequestParameterException) {// wuzl没有必传参数
            MissingServletRequestParameterException missingEx = (MissingServletRequestParameterException) exception;
            msg.failure(1, String.format("Param [%s] should not be empty", missingEx.getParameterName()));
        } else if (exception instanceof org.springframework.web.multipart.MaxUploadSizeExceededException) {
            msg.failure("Upload file too large");
        } else if (exception instanceof HttpMessageNotReadableException) {
            msg.failure("Parameters malformed");
		} else if (exception instanceof InvokingException) {
		    InvokingException ex = (InvokingException) exception;
            if (ex.getMsg() != null) {
                msg.failure(ex.getCode(), ex.getMsg());
            } else {
                msg.failure(ex.getCode(), "Error occurred");
            }
        } else {
            msg.failure(1, exception.getMessage());
            logger.error("【{}】接口异常：{}", request.getRequestURI(), exception.getMessage(), exception);
            isDetailErrorMsg = true;
        }
        String accept = request.getHeader("accept");
        if (takeAllRequestAsJSON || StringUtils.isEmpty(accept) || accept.contains("json")) {
            //json
            try {
                response.reset();
                response.setContentType("application/json; charset=utf-8");
                if (isDetailErrorMsg == true && StringUtils.isNotEmpty(jsonMsgError)) {
                    msg.failure(jsonMsgError);
                }
                response.getWriter().write(JSON.toJSONString(msg.returnMsg()));
                response.flushBuffer();
            } catch (Exception e) {
                logger.error("Capture json exception failed", e);
            }
            return new ModelAndView();
        } else if (handleNormalRequest) {
            //normal
            try {
                response.reset();
                response.setContentType("text/plain; charset=utf-8");
                if (StringUtils.isNotEmpty(normalMsgError)) {
                    response.getWriter().write(normalMsgError);
                } else {
                    response.getWriter().write(msg.getErrdesc());
                }
                response.flushBuffer();
            } catch (Exception e) {
                logger.error("Capture normal exception failed", e);
            }
            return new ModelAndView();
        }

        return null;
    }
    
    /**
     * 是否拦截正常请求，输出纯字符串的错误信息
     * 
     * @param handleNormalRequest
     */
    public static void setHandleNormalRequest(boolean handleNormalRequest) {
        CustomExceptionHandler.handleNormalRequest = handleNormalRequest;
    }
    
    /**
     * 是否将所有请求均视为json请求，并输出json
     * 
     * @param takeAllRequestAsJSON
     */
    public static void setTakeAllRequestAsJSON(boolean takeAllRequestAsJSON) {
        CustomExceptionHandler.takeAllRequestAsJSON = takeAllRequestAsJSON;
    }
    
    public static boolean isTakeAllRequestAsJSON() {
        return takeAllRequestAsJSON;
    }
    
    public static boolean isHandleNormalRequest() {
        return handleNormalRequest;
    }
    
    /**
     * 如果拦截正常请求，可设置统一脱敏的错误提示
     * 
     * @param normalMsgError
     */
    public static void setNormalMsgError(String normalMsgError) {
        CustomExceptionHandler.normalMsgError = normalMsgError;
    }
    
    public static void setJSONMsgError(String errorMsg) {
        CustomExceptionHandler.jsonMsgError = errorMsg;
    }
}
