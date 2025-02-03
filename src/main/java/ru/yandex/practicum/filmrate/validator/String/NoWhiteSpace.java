package ru.yandex.practicum.filmrate.validator.String;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoWhiteSpaceValidator.class)
@Past
public @interface NoWhiteSpace {
    String message() default "Поле не должно быть пустым или содержать пробелы";
    String value() default "";
}
