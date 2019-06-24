package com.yoloho.enhanced.spring.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

import com.yoloho.enhanced.common.util.FileUtil;

public class ResourceLocationHandler {
    final private static Logger logger = LoggerFactory.getLogger(ResourceLocationHandler.class.getSimpleName());
    
    private static boolean hasServletContextResource() {
        try {
            Class<?> clazz = Class.forName("org.springframework.web.context.support.ServletContextResource");
            if (clazz != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {
        }
        return false;
    }
    
    public static Resource processLocation(Resource location) {
        if (location == null) {
            return null;
        }
        logger.info("parsing property resource");
        Resource[] locations = new Resource[] {location};
        processLocations(locations);
        return locations[0];
    }
    
    public static void processLocations(Resource... locations) {
        if (locations != null) {
            logger.info("parsing property resources");
            boolean hasServlet = hasServletContextResource();
            for (int i = 0; i < locations.length; i++) {
                String path = null;
                if (ClassPathResource.class.isAssignableFrom(locations[i].getClass())) {
                    ClassPathResource location = (ClassPathResource) locations[i];
                    path = location.getPath();
                }
                if (path == null && hasServlet && ServletContextResource.class.isAssignableFrom(locations[i].getClass())) {
                    ServletContextResource location = (ServletContextResource) locations[i];
                    path = location.getPath();
                }
                if (path == null) {
                    continue;
                }
                try {
                    logger.info("尝试去加载 {}", path);
                    File file = FileUtil.getFileFromVariousPlace(path);
                    if (file != null) {
                        logger.info("替换资源: {}", path);
                        locations[i] = new FileSystemResource(file);
                    }
                } catch (Exception e) {
                    logger.error("加载失败", e);
                }
            }
        }
    }
}
