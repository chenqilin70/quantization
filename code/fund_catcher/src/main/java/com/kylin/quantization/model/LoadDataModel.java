package com.kylin.quantization.model;

public class LoadDataModel {
    public static final String LOCAL_FILE="local";
    public static final String HDFS_FILE="";

    public static final String OVERWRITE_TABLE="overwrite";
    public static final String NOT_OVERWRITE_TABLE="";

    private String local=HDFS_FILE;
    private String inpath;
    private String tableName;
    private String overwrite=NOT_OVERWRITE_TABLE;

    public LoadDataModel(String inpath, String tableName) {
        this.inpath = inpath;
        this.tableName = tableName;
    }

    public String getLocal() {
        return local;
    }

    public LoadDataModel setLocal(String local) {
        this.local = local;
        return this;
    }

    public String getInpath() {
        return inpath;
    }

    public LoadDataModel setInpath(String inpath) {
        this.inpath = inpath;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public LoadDataModel setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getOverwrite() {
        return overwrite;
    }

    public LoadDataModel setOverwrite(String overwrite) {
        this.overwrite = overwrite;
        return this;
    }
}
