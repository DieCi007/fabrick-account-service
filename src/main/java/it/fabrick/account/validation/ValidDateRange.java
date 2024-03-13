package it.fabrick.account.validation;

import it.fabrick.account.feature.account.contract.GetTransactionsFilters;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be in the future, after the specified amount of hours from now.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidDateRangeValidator.class)
public @interface ValidDateRange {

    String message() default "Invalid date range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, GetTransactionsFilters> {

    @Override
    public boolean isValid(final GetTransactionsFilters value, ConstraintValidatorContext context) {
        return !value.getFrom().isAfter(value.getTo());
    }

}
