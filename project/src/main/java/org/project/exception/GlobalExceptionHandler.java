package org.project.exception;

import jakarta.validation.ConstraintViolationException;
import org.project.util.Result;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 让这个类成为全局异常处理器
public class GlobalExceptionHandler {

    // 处理 @Valid 校验失败异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage(); // 获取第一个错误
            return Result.error(400, msg);
        }
        return Result.error(400, "参数校验失败");
    }

    // 处理 @Validated + @PathVariable 校验失败异常
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException ex) {
        String msg = ex.getMessage().split(":")[1].trim();
        return Result.error(400, msg);
    }

    // 可以继续添加其他异常处理方法
    
}

