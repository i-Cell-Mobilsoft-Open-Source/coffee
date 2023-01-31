package hu.icellmobilsoft.coffee.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.interceptor.InterceptorBinding;

/**
 * Customized {@link javax.transaction.Transactional} annotation with rollbackOn at {@link Throwable}.
 *
 * @author speter555
 * @since 1.14.0
 */
@javax.transaction.Transactional(rollbackOn = Throwable.class)
@InterceptorBinding
@Stereotype
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Transactional {
}
