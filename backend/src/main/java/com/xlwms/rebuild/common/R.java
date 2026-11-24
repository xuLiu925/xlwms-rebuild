package com.xlwms.rebuild.common;

/** 统一响应信封，与观察到的 {code:200, data, msg:"操作成功"} 一致。 */
public record R<T>(int code, T data, String msg) {
    public static <T> R<T> ok(T data) {
        return new R<>(200, data, "操作成功");
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, null, msg);
    }
}
