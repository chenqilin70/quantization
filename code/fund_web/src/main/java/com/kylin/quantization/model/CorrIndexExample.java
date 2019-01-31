package com.kylin.quantization.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CorrIndexExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public CorrIndexExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
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
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
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

        public Criteria andIndex1IsNull() {
            addCriterion("index1 is null");
            return (Criteria) this;
        }

        public Criteria andIndex1IsNotNull() {
            addCriterion("index1 is not null");
            return (Criteria) this;
        }

        public Criteria andIndex1EqualTo(String value) {
            addCriterion("index1 =", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1NotEqualTo(String value) {
            addCriterion("index1 <>", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1GreaterThan(String value) {
            addCriterion("index1 >", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1GreaterThanOrEqualTo(String value) {
            addCriterion("index1 >=", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1LessThan(String value) {
            addCriterion("index1 <", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1LessThanOrEqualTo(String value) {
            addCriterion("index1 <=", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1Like(String value) {
            addCriterion("index1 like", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1NotLike(String value) {
            addCriterion("index1 not like", value, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1In(List<String> values) {
            addCriterion("index1 in", values, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1NotIn(List<String> values) {
            addCriterion("index1 not in", values, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1Between(String value1, String value2) {
            addCriterion("index1 between", value1, value2, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex1NotBetween(String value1, String value2) {
            addCriterion("index1 not between", value1, value2, "index1");
            return (Criteria) this;
        }

        public Criteria andIndex2IsNull() {
            addCriterion("index2 is null");
            return (Criteria) this;
        }

        public Criteria andIndex2IsNotNull() {
            addCriterion("index2 is not null");
            return (Criteria) this;
        }

        public Criteria andIndex2EqualTo(String value) {
            addCriterion("index2 =", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2NotEqualTo(String value) {
            addCriterion("index2 <>", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2GreaterThan(String value) {
            addCriterion("index2 >", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2GreaterThanOrEqualTo(String value) {
            addCriterion("index2 >=", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2LessThan(String value) {
            addCriterion("index2 <", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2LessThanOrEqualTo(String value) {
            addCriterion("index2 <=", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2Like(String value) {
            addCriterion("index2 like", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2NotLike(String value) {
            addCriterion("index2 not like", value, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2In(List<String> values) {
            addCriterion("index2 in", values, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2NotIn(List<String> values) {
            addCriterion("index2 not in", values, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2Between(String value1, String value2) {
            addCriterion("index2 between", value1, value2, "index2");
            return (Criteria) this;
        }

        public Criteria andIndex2NotBetween(String value1, String value2) {
            addCriterion("index2 not between", value1, value2, "index2");
            return (Criteria) this;
        }

        public Criteria andCorrratioIsNull() {
            addCriterion("corrratio is null");
            return (Criteria) this;
        }

        public Criteria andCorrratioIsNotNull() {
            addCriterion("corrratio is not null");
            return (Criteria) this;
        }

        public Criteria andCorrratioEqualTo(BigDecimal value) {
            addCriterion("corrratio =", value, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioNotEqualTo(BigDecimal value) {
            addCriterion("corrratio <>", value, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioGreaterThan(BigDecimal value) {
            addCriterion("corrratio >", value, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("corrratio >=", value, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioLessThan(BigDecimal value) {
            addCriterion("corrratio <", value, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioLessThanOrEqualTo(BigDecimal value) {
            addCriterion("corrratio <=", value, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioIn(List<BigDecimal> values) {
            addCriterion("corrratio in", values, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioNotIn(List<BigDecimal> values) {
            addCriterion("corrratio not in", values, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("corrratio between", value1, value2, "corrratio");
            return (Criteria) this;
        }

        public Criteria andCorrratioNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("corrratio not between", value1, value2, "corrratio");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table CORR_INDEX
     *
     * @mbg.generated do_not_delete_during_merge Wed Jan 30 18:22:48 CST 2019
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table CORR_INDEX
     *
     * @mbg.generated Wed Jan 30 18:22:48 CST 2019
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