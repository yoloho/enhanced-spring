package com.yoloho.spring.util;

import java.util.regex.Pattern;

/**
 * for ENV -> placeholder under docker(eru)
 * 
 * @author jason
 *
 */
class EnvUtil {
    private static final String ENV_PROP_PREFIX = "ENV_PROP_";
    private static final Pattern dotPattern = Pattern.compile("([^_])_([^_])");
    private static final Pattern underlinePattern = Pattern.compile("([^_])__([^_])");
    
    public static String parseName(String name) {
        if (name.startsWith(ENV_PROP_PREFIX)) {
            String nameProp = name.substring(ENV_PROP_PREFIX.length());
            nameProp = dotPattern.matcher(nameProp).replaceAll("$1.$2");
            nameProp = underlinePattern.matcher(nameProp).replaceAll("$1_$2");
            return nameProp;
        }
        return null;
    }
}
