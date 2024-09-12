package vn.hoidanit.laptopshop.service.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = StrongPasswordValidator.class) //noi voi java biet rang annotation dung de validate du lieu
@Target({ ElementType.METHOD, ElementType.FIELD })//pham  vi la 1 class hay 1 truong
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {
    String message() default "Must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters.";
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
