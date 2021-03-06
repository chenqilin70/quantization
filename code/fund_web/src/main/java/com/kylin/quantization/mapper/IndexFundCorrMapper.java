package com.kylin.quantization.mapper;

import com.kylin.quantization.model.IndexFundCorr;
import com.kylin.quantization.model.IndexFundCorrExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndexFundCorrMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    long countByExample(IndexFundCorrExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    int deleteByExample(IndexFundCorrExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    int insert(IndexFundCorr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    int insertSelective(IndexFundCorr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    List<IndexFundCorr> selectByExample(IndexFundCorrExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    int updateByExampleSelective(@Param("record") IndexFundCorr record, @Param("example") IndexFundCorrExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Sun Jan 27 16:36:53 CST 2019
     */
    int updateByExample(@Param("record") IndexFundCorr record, @Param("example") IndexFundCorrExample example);
}