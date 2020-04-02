package com.megetood.validators;

import com.megetood.pojo.dto.PersonDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Date 2020/3/20 21:53
 */
public class PasswordValidator implements ConstraintValidator<PasswordEqual, PersonDTO> {
    private int min;
    private int max;


    @Override
    public boolean isValid(PersonDTO value, ConstraintValidatorContext context) {
        String password1 = value.getPassword1();
        String password2 = value.getPassword2();
        boolean match = password1.equals(password2);

        return match;
    }

    @Override
    public void initialize(PasswordEqual constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
}
