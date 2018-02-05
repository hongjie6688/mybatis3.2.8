package org.apache.ibatis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * need encry field
 *
 * @author zhanghj
 * @create 2018-01-22 15:07
 **/

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedEncry {
}
