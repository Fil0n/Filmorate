package ru.yandex.practicum.filmrate.validator.String;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoWhiteSpaceValidator.class)
public @interface NoWhiteSpace {
    String message() default "Поле не должно быть пустым или содержать пробелы";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
    String value() default "";
}
