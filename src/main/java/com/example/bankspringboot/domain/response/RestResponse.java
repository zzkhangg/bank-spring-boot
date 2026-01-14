package com.example.bankspringboot.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class RestResponse<T> {

  Object message;
  T data;
  int statusCode;

  public static <T> RestResponse<T> success(T data) {
    return RestResponse.<T>builder().data(data).statusCode(200).build();
  }

  public static RestResponse<?> error(String message, int status) {
    return RestResponse.builder().message(message).statusCode(status).build();
  }
}
