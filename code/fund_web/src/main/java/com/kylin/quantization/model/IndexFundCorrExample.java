package com.kylin.quantization.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IndexFundCorrExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public IndexFundCorrExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andFundcodeIsNull() {
            addCriterion("fundcode is null");
            return (Criteria) this;
        }

        public Criteria andFundcodeIsNotNull() {
            addCriterion("fundcode is not null");
            return (Criteria) this;
        }

        public Criteria andFundcodeEqualTo(String value) {
            addCriterion("fundcode =", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeNotEqualTo(String value) {
            addCriterion("fundcode <>", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeGreaterThan(String value) {
            addCriterion("fundcode >", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeGreaterThanOrEqualTo(String value) {
            addCriterion("fundcode >=", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeLessThan(String value) {
            addCriterion("fundcode <", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeLessThanOrEqualTo(String value) {
            addCriterion("fundcode <=", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeLike(String value) {
            addCriterion("fundcode like", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeNotLike(String value) {
            addCriterion("fundcode not like", value, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeIn(List<String> values) {
            addCriterion("fundcode in", values, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeNotIn(List<String> values) {
            addCriterion("fundcode not in", values, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeBetween(String value1, String value2) {
            addCriterion("fundcode between", value1, value2, "fundcode");
            return (Criteria) this;
        }

        public Criteria andFundcodeNotBetween(String value1, String value2) {
            addCriterion("fundcode not between", value1, value2, "fundcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeIsNull() {
            addCriterion("indexcode is null");
            return (Criteria) this;
        }

        public Criteria andIndexcodeIsNotNull() {
            addCriterion("indexcode is not null");
            return (Criteria) this;
        }

        public Criteria andIndexcodeEqualTo(String value) {
            addCriterion("indexcode =", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeNotEqualTo(String value) {
            addCriterion("indexcode <>", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeGreaterThan(String value) {
            addCriterion("indexcode >", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeGreaterThanOrEqualTo(String value) {
            addCriterion("indexcode >=", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeLessThan(String value) {
            addCriterion("indexcode <", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeLessThanOrEqualTo(String value) {
            addCriterion("indexcode <=", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeLike(String value) {
            addCriterion("indexcode like", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeNotLike(String value) {
            addCriterion("indexcode not like", value, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeIn(List<String> values) {
            addCriterion("indexcode in", values, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeNotIn(List<String> values) {
            addCriterion("indexcode not in", values, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeBetween(String value1, String value2) {
            addCriterion("indexcode between", value1, value2, "indexcode");
            return (Criteria) this;
        }

        public Criteria andIndexcodeNotBetween(String value1, String value2) {
            addCriterion("indexcode not between", value1, value2, "indexcode");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexIsNull() {
            addCriterion("correlationindex is null");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexIsNotNull() {
            addCriterion("correlationindex is not null");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexEqualTo(BigDecimal value) {
            addCriterion("correlationindex =", value, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexNotEqualTo(BigDecimal value) {
            addCriterion("correlationindex <>", value, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexGreaterThan(BigDecimal value) {
            addCriterion("correlationindex >", value, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("correlationindex >=", value, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexLessThan(BigDecimal value) {
            addCriterion("correlationindex <", value, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexLessThanOrEqualTo(BigDecimal value) {
            addCriterion("correlationindex <=", value, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexIn(List<BigDecimal> values) {
            addCriterion("correlationindex in", values, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexNotIn(List<BigDecimal> values) {
            addCriterion("correlationindex not in", values, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("correlationindex between", value1, value2, "correlationindex");
            return (Criteria) this;
        }

        public Criteria andCorrelationindexNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("correlationindex not between", value1, value2, "correlationindex");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated do_not_delete_during_merge Fri Jan 25 19:51:51 CST 2019
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table INDEX_FUND_CORR
     *
     * @mbg.generated Fri Jan 25 19:51:51 CST 2019
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}