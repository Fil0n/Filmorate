package ru.yandex.practicum.filmrate.validator.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(minDate);
    }

}
