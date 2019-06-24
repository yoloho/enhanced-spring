package com.yoloho.spring.support;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于自定义mvc参数处理，实现了 MvcArgument 的类放置在Controller#action参数中时，将会额外走接口中定义的init方法
 * 
 * @author jason
 *
 */
public interface MvcArgument {
    MvcArgument init(HttpServletRequest request);
}
