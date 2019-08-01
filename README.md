enhanced-spring
===

Some general configuration under Spring.

# Usage
```xml
<groupId>com.yoloho.enhanced</groupId>
<artifactId>enhanced-spring</artifactId>
<version>1.0.1</version>
```

# InitDefaults
## Feature
* Logging (log4j2)
* Exception handler
* Argument resolver (MvcArgument)
* Message converter (fastjson)
* Sentinel Support

## Integrating
If you use configuration based on xml files:

```xml
<enhanced-spring:init />
```

or through annotation under SpringBoot:

```java
@InitDefaults
```

## Logging
An initial configuration of log4j2 is generated if no `log4j2.xml` is given in class path. There are two properties to control it: 

```java
@InitDefaults(
    logLevel = "INFO", //default to INFO
    logConsole = true,
    logFile = true
)
```

When enabling logging to file, logs are separated into two streams as `app.log` which contains logs lower or equal than `INFO` and `error.log` which contains logs worse or equal than `ERROR`.

You can also change it through parameters of `-Dlog.console=true` and `-Dlog.file=true`.

## Exception Handler
If the project is a SpringMVC project you may want to use `ExceptionHandler`.

```java
@InitDefaults(
    takeAllRequestsAsJson = false,
    captureNormalRequest = true,
    errorMessageForNormalRequest = "Fix error response"
)
```

`takeAllRequestsAsJson=true` will take all requests as JSON request or only response json format response when detecting `Accept: application/json`.

`captureNormalRequest=true` will also handle exception for non-json requests.
By default response will contains the detail error message. If you want to hide it  just use `errorMessageForNormalRequest="your common error message"`.

Don't forget to introduce the configuration below under SpringBoot:
```java
@import(BaseWebMvcConfigurer.class)
```

If you want to customize:
```java
public class Web extends BaseWebMvcConfigurer {
}
```

## Argument Resolver
In some scenarios like common parameters, common logic related with ServletRequest you may feel like the way of `MvcArgument`.

If you put a parameter in your `RequestMapping` method which implements `MvcArgument`, `init` method will be automatically called before entering the method body.

## Message Converter
Reset to three converters: `ByteArrayHttpMessageConverter`, `StringHttpMessageConverter` and `FastJsonHttpMessageConverter`.

## Utilities
`SpringUtils` is used to get specified bean anywhere.
`PropertyUtil` is used to get specified property defined in properties file anywhere. (Must use propertyLocations)

## Sentinel Support
```java
@InitDefaults(
    projectName = "demo-project",
    sentinelPort = 8888
)
```

Dependencies to add:

```xml
<dependency>
	<groupId>com.alibaba.csp</groupId>
	<artifactId>sentinel-transport-netty-http</artifactId>
</dependency>
<dependency>
	<groupId>com.alibaba.csp</groupId>
	<artifactId>sentinel-web-servlet</artifactId>
</dependency>
<dependency>
	<groupId>com.alibaba.csp</groupId>
	<artifactId>sentinel-datasource-zookeeper</artifactId>
</dependency>
```

**web.xml**
```xml
<filter>
	<filter-name>SentinelCommonFilter</filter-name>
	<filter-class>com.alibaba.csp.sentinel.adapter.servlet.CommonFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>SentinelCommonFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

Or through configuration class:
```java
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");
        registration.setName("sentinelFilter");
        registration.setOrder(1);

        return registration;
    }
}
```

Following properties also need to set to initialize sentinel:

name | memo
--- | ---
sentinel.zookeeper | sentinel's zookeeper datasource address, if empty `dubbo.registry.url` will be the fallback.
sentinel.dashboard | dashboard address, like ip:port or domain:port, default to sentinel.dayima.org:80

### UrlCleaner/UrlBlockHandler
When you want to use `UrlCleaner` or `UrlBlockHandler` just inject it into spring and it will be registered automatically.

## enhanced-common:property-placeholder
Almost same as `context:property-placeholder`. But enable ability of `PropertyUtil` and custom location overriding.

## ENV override
For container or other scenario environment variables are better way to load configuration. So we support overriding properties by environment variables like:

`ERU_PROP_jdbc_url` will override property `jdbc.url`

### Notes

* dots(.) are not allowed in environment variables so it is replaced by underline "_"
* If underline is really needed you should double it like `__` which will be converted into `_`.
* Use prefix `ERU_PROP_`
* Generally overriding is in the air but for the properties `dubbo.*` they are set into System.properties to make them take effect
* ENV "ERU_POD" must not be empty (If you are already in ERU it is set automatically)

# Change Log
## 1.0.1
* Supplement documents
* Remove deprecated files

## 1.0.0
* Initial to open source


