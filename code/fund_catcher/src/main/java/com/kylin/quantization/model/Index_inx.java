package com.kylin.quantization.model;

/**
 * ClassName: Index_inx
 * Description:
 * Author: aierxuan
 * Date: 2019-02-15 14:22
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class Index_inx {
    private Double percent;
    private String rowkey;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
