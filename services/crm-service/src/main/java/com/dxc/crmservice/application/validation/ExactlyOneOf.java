package com.dxc.crmservice.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/*
    this annotation is used to validate that exactly one of the fields in the object is not null
*/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ExactlyOneOf.List.class)
@Constraint(validatedBy = ExactlyOneOfValidator.class)
public @interface ExactlyOneOf {

    String message() default "Exactly one group must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ExactlyOneOf[] value();
    }
}