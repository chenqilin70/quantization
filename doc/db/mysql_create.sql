create table INDEX_FUND_CORR(
  fundcode VARCHAR(20),
  indexcode VARCHAR(20),
  correlationindex DECIMAL(28,6)
)DEFAULT CHARSET=utf8;

create table FUND(
  rowkey     varchar(100),
  fundcode varchar(100),
  fundtype varchar(100),
  tgfl varchar(100),
  zgrgfl varchar(100),
  bbms varchar(100),
  glfl varchar(100),
  clylfh varchar(100),
  yjbjjz varchar(100),
  fhgm varchar(100),
  gzbd varchar(100),
  zcgm varchar(100),
  zjbbq varchar(100),
  fxrq varchar(100),
  jjtgr	 varchar(100),
  jjqc varchar(100),
  zgshfl varchar(100),
  jjjc varchar(100),
  jjlx varchar(100),
  jjglr varchar(100),
  zgsgfl varchar(100),
  xsfwfl varchar(100),
  dbjg varchar(100),
  jjdm varchar(100),
  clrqgm varchar(100),
  jjjlr varchar(100)
)DEFAULT CHARSET=utf8;

create table CORR_INDEX(
  index1 varchar(20),
  index2 varchar(20),
  corrratio DECIMAL(28,6)
)DEFAULT CHARSET=utf8;