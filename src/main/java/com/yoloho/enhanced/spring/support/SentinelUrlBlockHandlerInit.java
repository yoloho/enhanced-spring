package com.yoloho.enhanced.spring.support;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.csp.sentinel.adapter.servlet.callback.DefaultUrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.yoloho.enhanced.common.support.MsgBean;

/**
 * set custom url block handler
 * 
 * @author jason
 *
 */
public class SentinelUrlBlockHandlerInit {
    private static final Logger logger = LoggerFactory.getLogger(SentinelUrlBlockHandlerInit.class.getSimpleName());
    
    UrlBlockHandler sentinelUrlBlockHandler = new DefaultUrlBlockHandler();
    UrlBlockHandler defaultCustomUrlBlockHandler = new UrlBlockHandler() {
        
        @Override
        public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException e) throws IOException {
            String accept = request.getHeader("accept");
            if (CustomExceptionHandler.isTakeAllRequestAsJSON() 
                    || StringUtils.isEmpty(accept)
                    || accept.contains("json")) {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println(defaultJsonError);
                out.flush();
                out.close();
                return;
            }
            sentinelUrlBlockHandler.blocked(request, response, e);
        }

    };
    String defaultJsonError = null;
    {
        MsgBean msg = new MsgBean(17001, "服务不可用");
        defaultJsonError = JSON.toJSONString(msg.returnMsg());
    }
    
    @Autowired(required = false)
    UrlBlockHandler customUrlBlockHandler;
    @Autowired(required = false)
    UrlCleaner urlCleaner;
    
    @PostConstruct
    public void init() {
        if (customUrlBlockHandler != null) {
            WebCallbackManager.setUrlBlockHandler(customUrlBlockHandler);
            logger.info("找到自定义url阻断处理器");
        } else {
            WebCallbackManager.setUrlBlockHandler(defaultCustomUrlBlockHandler);
            logger.info("无自定义url阻断处理器，采用默认");
        }
        if (urlCleaner != null) {
            WebCallbackManager.setUrlCleaner(urlCleaner);
            logger.info("找到自定义url清洗处理器");
        } else {
            logger.info("无自定义url清洗处理器，采用默认");
        }
    }

}
