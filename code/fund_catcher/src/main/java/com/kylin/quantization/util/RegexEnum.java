package com.kylin.quantization.util;

/**
 * ClassName: RegexEnum
 * Description:
 * Author: aierxuan
 * Date: 2019-01-14 14:57
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public enum RegexEnum {
    NET_VAL_REG("-*\\d+\\.\\d+");

    RegexEnum(String val) {
        this.val = val;
    }

    private String val;

    public String val() {
        return val;
    }
}
