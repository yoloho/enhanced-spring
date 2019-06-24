package com.yoloho.enhanced.spring.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class.getSimpleName());

    public interface CustomProperties {
        /**
         * 针对属性名做获取
         * 
         * @param name
         * @return
         */
        String getProperty(String name);
        /**
         * 解析
         * 
         * @param name
         * @return
         */
        String resolveProperty(String name);
    }

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static List<CustomProperties> configurers = Lists.newArrayList();

    public static void addPlaceHolderConfigurer(CustomProperties config) {
        // 倒序插入，这样正序遍历即可实现后覆盖前
        lock.writeLock().lock();
        try {
            configurers.add(0, config);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    /**
     * 如果name中含有${}，则做解析
     * 
     * @param name
     * @return null for fail
     */
    public static String resolveProperty(String name) {
        lock.readLock().lock();
        try {
            int count = configurers.size();
            for (int i = 0; i < count; i++) {
                String val = configurers.get(i).resolveProperty(name);
                if (val != null) {
                    return val;
                }
            }
            return name;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get property by name
     * 
     * @param name
     * @return null for not exists or fail
     */
    public static String getProperty(String name) {
        lock.readLock().lock();
        try {
            int count = configurers.size();
            for (int i = 0; i < count; i++) {
                String val = configurers.get(i).getProperty(name);
                if (val != null) {
                    return val;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static int getInteger(String name, int defaultValue) {
        return (int)getLong(name, defaultValue);
    }

    /**
     * 
     * @param name
     * @return
     */
    public static int getInteger(String name) {
        return getInteger(name, 0);
    }

    public static long getLong(String name, long defaultValue) {
        String value = getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        return NumberUtils.toLong(value, defaultValue);
    }

    public static long getLong(String name) {
        return getLong(name, 0l);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        String value = getProperty(name, defaultValue ? "true" : "false");
        return Boolean.parseBoolean(value);
    }

    public static boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public static Date getDate(String name, String format) {
        String value = getProperty(name);
        if (value == null) {
            return null;
        }
        FastDateFormat fastDateFormat = FastDateFormat.getInstance(format);
        try {
            return fastDateFormat.parse(value);
        } catch (ParseException e) {
            logger.error("日期类型placeholder解析失败: {} of {}", value, format, e);
        }
        return null;
    }

}