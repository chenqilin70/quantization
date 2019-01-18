package com.kylin.quantization.model;

import java.math.BigDecimal;

/**
 * ClassName: Netval
 * Description:
 * Author: aierxuan
 * Date: 2019-01-17 17:59
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class Netval {
    private String rowkey;
    private BigDecimal DWJZ         ;
    private BigDecimal LJJZ	      ;
    private String FHSP	      ;
    private String FHFCZ	      ;
    private String FHFCBZ	      ;
    private String FSRQ	      ;
    private String NAVTYPE      ;
    private String SDATE	      ;
    private String DTYPE	      ;
    private String ACTUALSYI    ;
    private String SHZT	      ;
    private String SGZT	      ;
    private String JZZZL	      ;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public BigDecimal getDWJZ() {
        return DWJZ;
    }

    public void setDWJZ(BigDecimal DWJZ) {
        this.DWJZ = DWJZ;
    }

    public BigDecimal getLJJZ() {
        return LJJZ;
    }

    public void setLJJZ(BigDecimal LJJZ) {
        this.LJJZ = LJJZ;
    }

    public String getFHSP() {
        return FHSP;
    }

    public void setFHSP(String FHSP) {
        this.FHSP = FHSP;
    }

    public String getFHFCZ() {
        return FHFCZ;
    }

    public void setFHFCZ(String FHFCZ) {
        this.FHFCZ = FHFCZ;
    }

    public String getFHFCBZ() {
        return FHFCBZ;
    }

    public void setFHFCBZ(String FHFCBZ) {
        this.FHFCBZ = FHFCBZ;
    }

    public String getFSRQ() {
        return FSRQ;
    }

    public void setFSRQ(String FSRQ) {
        this.FSRQ = FSRQ;
    }

    public String getNAVTYPE() {
        return NAVTYPE;
    }

    public void setNAVTYPE(String NAVTYPE) {
        this.NAVTYPE = NAVTYPE;
    }

    public String getSDATE() {
        return SDATE;
    }

    public void setSDATE(String SDATE) {
        this.SDATE = SDATE;
    }

    public String getDTYPE() {
        return DTYPE;
    }

    public void setDTYPE(String DTYPE) {
        this.DTYPE = DTYPE;
    }

    public String getACTUALSYI() {
        return ACTUALSYI;
    }

    public void setACTUALSYI(String ACTUALSYI) {
        this.ACTUALSYI = ACTUALSYI;
    }

    public String getSHZT() {
        return SHZT;
    }

    public void setSHZT(String SHZT) {
        this.SHZT = SHZT;
    }

    public String getSGZT() {
        return SGZT;
    }

    public void setSGZT(String SGZT) {
        this.SGZT = SGZT;
    }

    public String getJZZZL() {
        return JZZZL;
    }

    public void setJZZZL(String JZZZL) {
        this.JZZZL = JZZZL;
    }
}
