package com.kylin.quantization.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ClassName: IndexFundCorr
 * Description:
 * Author: aierxuan
 * Date: 2019-01-24 16:45
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class IndexFundCorr implements Serializable{
    private String fundcode ;
    private String  indexcode ;
    private BigDecimal correlationindex;

    public IndexFundCorr(String fundcode, String indexcode, BigDecimal correlationindex) {
        this.fundcode = fundcode;
        this.indexcode = indexcode;
        this.correlationindex = correlationindex;
    }

    public IndexFundCorr() {
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public String getIndexcode() {
        return indexcode;
    }

    public void setIndexcode(String indexcode) {
        this.indexcode = indexcode;
    }

    public BigDecimal getCorrelationindex() {
        return correlationindex;
    }

    public void setCorrelationindex(BigDecimal correlationindex) {
        this.correlationindex = correlationindex;
    }
}
