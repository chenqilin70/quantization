
CREATE DATABASE IF NOT EXISTS fund_catcher;
CREATE EXTERNAL TABLE fund_catcher.fund(
  key       string COMMENT 'rowkey',
	jjjc       string COMMENT '基金简称',
	clylfh	   string COMMENT '成立来分红',
	zgrgfl	   string COMMENT '最高认购费率',
	fundcode   string COMMENT '基金代码',
	jjqc	   string COMMENT '基金全称',
	sortpinyin string COMMENT '拼音简称',
	fhgm	   string COMMENT '份额规模',
	fxrq	   string COMMENT '发行日期',
	clrqgm	   string COMMENT '成立日期/规模',
	zcgm	   string COMMENT '资产规模',
	glfl	   string COMMENT '管理费率',
	yjbjjz	   string COMMENT '业绩比较基准',
	jjdm	   string COMMENT '基金代码',
	jjtgr	   string COMMENT '基金托管人',
	jjjlr	   string COMMENT '基金经理人',
	zgshfl	   string COMMENT '最高赎回费率',
	gzbd	   string COMMENT '跟踪标的',
	jjlx	   string COMMENT '基金类型',
	tgfl	   string COMMENT '托管费率',
	fundtype   string COMMENT '基金类型',
	sortname   string COMMENT '基金简称',
	jjglr	   string COMMENT '基金管理人',
	zgsgfl	   string COMMENT '最高申购费率',
	pinyin	   string COMMENT '基金拼音',
	xsfwfl	   string COMMENT '销售服务费率',
	zjbbq	   string COMMENT '最近保本期'
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
(
	"hbase.columns.mapping" =
	":key,baseinfo:jjjc,baseinfo:clylfh,baseinfo:zgrgfl,baseinfo:fundcode,baseinfo:jjqc,baseinfo:sortpinyin,baseinfo:fhgm,baseinfo:fxrq,baseinfo:clrqgm,baseinfo:zcgm,baseinfo:glfl,baseinfo:yjbjjz,baseinfo:jjdm,baseinfo:jjtgr,baseinfo:jjjlr,baseinfo:zgshfl,baseinfo:gzbd,baseinfo:jjlx,baseinfo:tgfl,baseinfo:fundtype,baseinfo:sortname,baseinfo:jjglr,baseinfo:zgsgfl,baseinfo:pinyin,baseinfo:xsfwfl,baseinfo:zjbbq"
)
TBLPROPERTIES("hbase.table.name" = "fund");







CREATE EXTERNAL TABLE fund_catcher.netval(
  key        string,
  DWJZ	   string,
  LJJZ	   string,
  FHSP	   string,
  FHFCZ	   string,
  FHFCBZ	   string,
  FSRQ	   string,
  NAVTYPE	   string,
  SDATE	   string,
  DTYPE	   string,
  ACTUALSYI  string,
  SHZT	   string,
  SGZT	   string,
  JZZZL	   string
)STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
(
	"hbase.columns.mapping" =
	":key,baseinfo:DWJZ,baseinfo:LJJZ,baseinfo:FHSP,baseinfo:FHFCZ,baseinfo:FHFCBZ,baseinfo:FSRQ,baseinfo:NAVTYPE,baseinfo:SDATE,baseinfo:DTYPE,baseinfo:ACTUALSYI,baseinfo:SHZT,baseinfo:SGZT,baseinfo:JZZZL"
)
TBLPROPERTIES("hbase.table.name" = "netval");







