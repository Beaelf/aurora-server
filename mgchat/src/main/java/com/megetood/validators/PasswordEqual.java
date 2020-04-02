package com.megetood.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Date 2020/3/20 21:47
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordEqual {
    int min() default 3;
    int max() default 4;
    String message() default "password are not equal";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
