package com.example.external;

import java.lang.reflect.InvocationTargetException;

class ReflectionUtils {
  static <T> T newInstance(Class<T> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new IllegalStateException("Cannot instantiate type of " + clazz);
    }
  }
}
