package com.example.qrecyclerviewpaging;

/**
 * 页面类型（0-原生，1-H5）
 */
public enum PageTypeEnum {
    NATIVE(0),
    H5(1);

    private int value;

    PageTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
