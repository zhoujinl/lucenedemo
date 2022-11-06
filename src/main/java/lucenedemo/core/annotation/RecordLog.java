package lucenedemo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要记录的日志的注解
 * 在需要记录日志的controller上添加该注解，可以记录日志
 *
 * @author zrx
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordLog {

	String value() default "";
}
