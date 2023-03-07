package com.wgx.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wgx
 * @since 2023/3/7 16:44
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private Set<Integer> set;

    //初始化
    @Override
    public void initialize(ListValue constraintAnnotation) {
        set = new HashSet<>();
        int[] values = constraintAnnotation.values();
        set.addAll(Arrays.stream(values).boxed().collect(Collectors.toList()));
    }

    //判断是否校验成功
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
