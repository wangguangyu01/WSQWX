package com.tencent.wxcloudrun.exception;

import java.lang.reflect.Type;

public class JsonException extends RuntimeException {

    public JsonException() {
        super();
    }

    public JsonException(String msg) {
        super(msg);
    }

    public JsonException(Class clazz, Throwable throwable) {
        super(clazz.getName(), throwable);
    }

    public JsonException(Type type, Throwable throwable) {
        super(type.getTypeName(), throwable);
    }


    public JsonException(Throwable throwable) {
        super(throwable);
    }
}
