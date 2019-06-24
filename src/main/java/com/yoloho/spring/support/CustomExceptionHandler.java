package com.yoloho.spring.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.alibaba.fastjson.JSON;
import com.yoloho.common.support.MsgBean;
import com.yoloho.spring.exception.InvokingException;

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
    
    public CustomExceptionHandler() {
        super();
        setOrder(-1);
        logger.info("init default exception handler");
    }
    
    @Override
    @ResponseBody
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception exception) {
        MsgBean msg = new MsgBean();
        if (exception instanceof TypeMismatchException) {// wuzl类型不正确
            TypeMismatchException typeEx = (TypeMismatchException) exception;
            msg.failure(1, String.format("参数值[%s]不是正确的类型", typeEx.getValue()));
        } else if (exception instanceof MissingServletRequestParameterException) {// wuzl没有必传参数
            MissingServletRequestParameterException missingEx = (MissingServletRequestParameterException) exception;
            msg.failure(1, String.format("参数[%s]不可以为空", missingEx.getParameterName()));
        } else if (exception instanceof org.springframework.web.multipart.MaxUploadSizeExceededException) {
            msg.failure("上传文件过大，已经超过最大限度");
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
        }
        String accept = request.getHeader("accept");
        if (takeAllRequestAsJSON || StringUtils.isEmpty(accept) || accept.contains("json")) {
            //json
            try {
                response.reset();
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().write(JSON.toJSONString(msg.returnMsg()));
                response.flushBuffer();
            } catch (Exception e) {
                logger.error("异常捕获器错误1", e);
            }
            return new ModelAndView();
        } else if (handleNormalRequest) {
            //normal
            try {
                response.reset();
                response.setContentType("text/plain; charset=utf-8");
                if (normalMsgError != null) {
                    response.getWriter().write(normalMsgError);
                } else {
                    response.getWriter().write(msg.getErrdesc());
                }
                response.flushBuffer();
            } catch (Exception e) {
                logger.error("异常捕获器错误", e);
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
}
