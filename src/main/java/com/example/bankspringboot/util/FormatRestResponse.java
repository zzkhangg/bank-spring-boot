package com.example.bankspringboot.util;

import com.example.bankspringboot.domain.response.RestResponse;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      @NonNull MethodParameter returnType,
      @NonNull MediaType selectedContentType,
      @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response) {
    HttpServletResponse httpServletResponse =
        ((ServletServerHttpResponse) response).getServletResponse();

    if (body instanceof byte[]) {
      return body;
    }

    if (body == null && httpServletResponse.getStatus() == HttpStatus.NO_CONTENT.value()) {
      return null; // keep 204 No Content
    }

    if (body instanceof String) {
      return body;
    }

    int statusCode = httpServletResponse.getStatus();
    if (statusCode >= 400) {
      return body;
    }

    return RestResponse.success(body);
  }
}
