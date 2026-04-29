package com.dxc.crmservice.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

public class ExactlyOneOfValidator
        implements ConstraintValidator<ExactlyOneOf, Object> {

    private String[] fields;

    @Override
    public void initialize(ExactlyOneOf annotation) {
        this.fields = annotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        int count = 0;

        try {
            for (String field : fields) {
                Method method = value.getClass().getMethod(field);
                Object fieldValue = method.invoke(value);

                if (fieldValue != null) {
                    count++;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return count == 1;
    }
}