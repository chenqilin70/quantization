package com.kylin.quantization.mapper;

import com.kylin.quantization.model.CorrIndex;
import com.kylin.quantization.model.CorrIndexExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CorrIndexMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    long countByExample(CorrIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    int deleteByExample(CorrIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    int insert(CorrIndex record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    int insertSelective(CorrIndex record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    List<CorrIndex> selectByExample(CorrIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    int updateByExampleSelective(@Param("record") CorrIndex record, @Param("example") CorrIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    int updateByExample(@Param("record") CorrIndex record, @Param("example") CorrIndexExample example);
}