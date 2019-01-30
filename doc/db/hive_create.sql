
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


CREATE EXTERNAL TABLE fund_catcher.index(
  key		string,
  ma10		string,
  ma30		string,
  dea		string,
  psy		string,
  ma5		string,
  bias2		string,
  bias3		string,
  bias1		string,
  close		string,
  macd		string,
  timestamp	string,
  wr10		string,
  kdjd		string,
  volume		string,
  dif		string,
  kdjk		string,
  low		string,
  percent		string,
  wr6		string,
  turnoverrate	string,
  rsi1		string,
  rsi2		string,
  rsi3		string,
  psyma		string,
  high		string,
  ub		string,
  lb		string,
  chg		string,
  ma20		string,
  cci		string,
  open		string,
  kdjj		string
)STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES
(
	"hbase.columns.mapping" =
	":key,baseinfo:ma10,baseinfo:ma30,baseinfo:dea,baseinfo:psy,baseinfo:ma5,baseinfo:bias2,baseinfo:bias3,baseinfo:bias1,baseinfo:close,baseinfo:macd,baseinfo:timestamp,baseinfo:wr10,baseinfo:kdjd,baseinfo:volume,baseinfo:dif,baseinfo:kdjk,baseinfo:low,baseinfo:percent,baseinfo:wr6,baseinfo:turnoverrate,baseinfo:rsi1,baseinfo:rsi2,baseinfo:rsi3,baseinfo:psyma,baseinfo:high,baseinfo:ub,baseinfo:lb,baseinfo:chg,baseinfo:ma20,baseinfo:cci,baseinfo:open,baseinfo:kdjj"
)
TBLPROPERTIES("hbase.table.name" = "index");







