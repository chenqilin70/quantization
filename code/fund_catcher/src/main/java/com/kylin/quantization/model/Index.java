package com.kylin.quantization.model;

import java.math.BigDecimal;

/**
 * ClassName: Index
 * Description:
 * Author: aierxuan
 * Date: 2019-01-17 11:45
 * History:
 * <author> <time> <version>    <desc>
 * 作者姓名 修改时间    版本号 描述
 */
public class Index {
    private String rowkey;
    private BigDecimal ma10              ;
    private BigDecimal ma30		 ;
    private BigDecimal dea		 ;
    private BigDecimal psy		 ;
    private BigDecimal ma5		 ;
    private BigDecimal bias2		 ;
    private BigDecimal bias3		 ;
    private BigDecimal bias1		 ;
    private BigDecimal close		 ;
    private BigDecimal macd		 ;
    private BigDecimal timestamp	 ;
    private BigDecimal wr10		 ;
    private BigDecimal kdjd		 ;
    private BigDecimal volume		 ;
    private BigDecimal dif		 ;
    private BigDecimal kdjk		 ;
    private BigDecimal low		 ;
    private BigDecimal percent		 ;
    private BigDecimal wr6		 ;
    private BigDecimal turnoverrate	 ;
    private BigDecimal rsi1		 ;
    private BigDecimal rsi2		 ;
    private BigDecimal rsi3		 ;
    private BigDecimal psyma		 ;
    private BigDecimal high		 ;
    private BigDecimal ub		 ;
    private BigDecimal lb		 ;
    private BigDecimal chg		 ;
    private BigDecimal ma20		 ;
    private BigDecimal cci		 ;
    private BigDecimal open		 ;
    private BigDecimal kdjj              ;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public BigDecimal getMa10() {
        return ma10;
    }

    public void setMa10(BigDecimal ma10) {
        this.ma10 = ma10;
    }

    public BigDecimal getMa30() {
        return ma30;
    }

    public void setMa30(BigDecimal ma30) {
        this.ma30 = ma30;
    }

    public BigDecimal getDea() {
        return dea;
    }

    public void setDea(BigDecimal dea) {
        this.dea = dea;
    }

    public BigDecimal getPsy() {
        return psy;
    }

    public void setPsy(BigDecimal psy) {
        this.psy = psy;
    }

    public BigDecimal getMa5() {
        return ma5;
    }

    public void setMa5(BigDecimal ma5) {
        this.ma5 = ma5;
    }

    public BigDecimal getBias2() {
        return bias2;
    }

    public void setBias2(BigDecimal bias2) {
        this.bias2 = bias2;
    }

    public BigDecimal getBias3() {
        return bias3;
    }

    public void setBias3(BigDecimal bias3) {
        this.bias3 = bias3;
    }

    public BigDecimal getBias1() {
        return bias1;
    }

    public void setBias1(BigDecimal bias1) {
        this.bias1 = bias1;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getMacd() {
        return macd;
    }

    public void setMacd(BigDecimal macd) {
        this.macd = macd;
    }

    public BigDecimal getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(BigDecimal timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getWr10() {
        return wr10;
    }

    public void setWr10(BigDecimal wr10) {
        this.wr10 = wr10;
    }

    public BigDecimal getKdjd() {
        return kdjd;
    }

    public void setKdjd(BigDecimal kdjd) {
        this.kdjd = kdjd;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getDif() {
        return dif;
    }

    public void setDif(BigDecimal dif) {
        this.dif = dif;
    }

    public BigDecimal getKdjk() {
        return kdjk;
    }

    public void setKdjk(BigDecimal kdjk) {
        this.kdjk = kdjk;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public BigDecimal getWr6() {
        return wr6;
    }

    public void setWr6(BigDecimal wr6) {
        this.wr6 = wr6;
    }

    public BigDecimal getTurnoverrate() {
        return turnoverrate;
    }

    public void setTurnoverrate(BigDecimal turnoverrate) {
        this.turnoverrate = turnoverrate;
    }

    public BigDecimal getRsi1() {
        return rsi1;
    }

    public void setRsi1(BigDecimal rsi1) {
        this.rsi1 = rsi1;
    }

    public BigDecimal getRsi2() {
        return rsi2;
    }

    public void setRsi2(BigDecimal rsi2) {
        this.rsi2 = rsi2;
    }

    public BigDecimal getRsi3() {
        return rsi3;
    }

    public void setRsi3(BigDecimal rsi3) {
        this.rsi3 = rsi3;
    }

    public BigDecimal getPsyma() {
        return psyma;
    }

    public void setPsyma(BigDecimal psyma) {
        this.psyma = psyma;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getUb() {
        return ub;
    }

    public void setUb(BigDecimal ub) {
        this.ub = ub;
    }

    public BigDecimal getLb() {
        return lb;
    }

    public void setLb(BigDecimal lb) {
        this.lb = lb;
    }

    public BigDecimal getChg() {
        return chg;
    }

    public void setChg(BigDecimal chg) {
        this.chg = chg;
    }

    public BigDecimal getMa20() {
        return ma20;
    }

    public void setMa20(BigDecimal ma20) {
        this.ma20 = ma20;
    }

    public BigDecimal getCci() {
        return cci;
    }

    public void setCci(BigDecimal cci) {
        this.cci = cci;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getKdjj() {
        return kdjj;
    }

    public void setKdjj(BigDecimal kdjj) {
        this.kdjj = kdjj;
    }
}
