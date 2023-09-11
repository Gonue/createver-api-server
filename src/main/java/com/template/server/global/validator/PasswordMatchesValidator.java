package com.template.server.global.validator;

import com.template.server.domain.member.dto.request.MemberJoinRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, MemberJoinRequest> {

   @Override
   public void initialize(final PasswordMatches constraintAnnotation) {
   }

   @Override
   public boolean isValid(final MemberJoinRequest request, final ConstraintValidatorContext context) {
      if (!request.getPassword().equals(request.getCheckPassword())) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                 .addPropertyNode("checkPassword")
                 .addConstraintViolation();
          return false;
      }
      return true;
   }
}
