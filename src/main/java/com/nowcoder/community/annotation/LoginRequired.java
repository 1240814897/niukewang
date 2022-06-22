package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//作用的范围（方法）
@Target(ElementType.METHOD)
//保留的时间有效的时间
@Retention(RetentionPolicy.RUNTIME)
//自定义注解
public @interface LoginRequired {
}
