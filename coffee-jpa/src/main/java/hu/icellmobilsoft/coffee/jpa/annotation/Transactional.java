package hu.icellmobilsoft.coffee.jpa.annotation;

import jakarta.enterprise.inject.Stereotype;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Customized {@link jakarta.transaction.Transactional} annotation with rollbackOn at {@link Throwable}.
 *
 * @author speter555
 * @since 2.0.0
 */
@jakarta.transaction.Transactional(rollbackOn = Throwable.class)
@InterceptorBinding
@Stereotype
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Transactional {
}
