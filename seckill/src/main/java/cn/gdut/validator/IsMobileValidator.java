package cn.gdut.validator;

import cn.gdut.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号码验证的工具，会被注解@isModel所使用
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    // 用于获取验证的字段是否为空
    private boolean required = false;



    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        // 如果所检验的字段可以为空
        if (required){
            return ValidatorUtil.isMobile(s);
        }
        else {
            if (StringUtils.isEmpty(s))
                return true;
            else
                return ValidatorUtil.isMobile(s);
        }
    }
}
