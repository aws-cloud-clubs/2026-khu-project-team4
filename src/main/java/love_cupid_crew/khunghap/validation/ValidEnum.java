package love_cupid_crew.khunghap.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidEnumValidator.class)
@Target({ FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValidEnum {
    String message() default "유효하지 않은 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> enumClass();
}

