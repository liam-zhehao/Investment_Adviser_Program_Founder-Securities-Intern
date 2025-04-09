-- 电子协议签署记录表
create table osoadata.t_comb_epaper_sign_record (
	id number not null,
	create_date date,
	update_date date,
	client_id varchar2(20 char) not null,
	combine_code varchar2(20 char) not null,
	template_id varchar2(20 char) not null,
	ext_serial_no varchar2(256 char) not null,
	template_name varchar2(20 char) not null,
	version_no varchar2(20 char),
	template_type varchar2(20 char) not null,
	image_no varchar2(20 char) not null,
	epaper_id varchar2(256 char) not null,
	constraint pk_comb_epaper_sign_record primary key (id)
);

-- add comments
comment on column osoadata.t_comb_epaper_sign_record.id is '主键';
comment on column osoadata.t_comb_epaper_sign_record.create_date is '创建日期';
comment on column osoadata.t_comb_epaper_sign_record.update_date is '更新日期';
comment on column osoadata.t_comb_epaper_sign_record.client_id is '客户id';
comment on column osoadata.t_comb_epaper_sign_record.combine_code is '组合代码';
comment on column osoadata.t_comb_epaper_sign_record.template_id is '模板编码';
comment on column osoadata.t_comb_epaper_sign_record.ext_serial_no is '外部流水号';
comment on column osoadata.t_comb_epaper_sign_record.template_name is '模板名称';
comment on column osoadata.t_comb_epaper_sign_record.version_no is '版本号';
comment on column osoadata.t_comb_epaper_sign_record.template_type is '模板类型';
comment on column osoadata.t_comb_epaper_sign_record.image_no is '图片编号';
comment on column osoadata.t_comb_epaper_sign_record.epaper_id is '协议编码';
comment on table osoadata.t_comb_epaper_sign_record is '组合电子协议签署表';

create sequence osoadata.seq_comb_epaper_sign_record
start with 1
nomaxvalue
increment by 1
nocycle
cache 20
order;

-- 组合操作记录表
create table osoadata.t_comb_request_record (
  id number(36,0) not null,
  client_id varchar2(20 char) not null,
  client_name varchar2(60 char),
  mobile varchar2(20 char),
  comb_code varchar2(20 char) not null,
  comb_name varchar2(100 char) not null,
  balance number(19,2),
  fund_account varchar2(20 char) not null,
  branch_no varchar2(20 char),
  sys_no varchar2(20 char),
  op_station varchar2(255 char),
  resp varchar2(1000 char) not null,
  oper_code varchar2(2 char) not null,
  oper_desc varchar2(20 char) not null,
  oper_ip varchar2(20 char),
  create_date date not null,
  update_date date not null,
  create_month number(8,0) not null,
  redeem_ratio number(10,8),
  trace_id varchar2(100 char),
  orig_comb_request_no varchar2(255 char),
  comb_request_no varchar2(255 char),
  error_no varchar2(20 char),
  error_info varchar2(1000 char),
  primary key (id)
);

-- add comments
comment on column osoadata.t_comb_request_record.id is '自增主键';
comment on column osoadata.t_comb_request_record.client_id is '客户id';
comment on column osoadata.t_comb_request_record.client_name is '客户名称';
comment on column osoadata.t_comb_request_record.mobile is '激活手机号';
comment on column osoadata.t_comb_request_record.comb_code is '组合代码';
comment on column osoadata.t_comb_request_record.comb_name is '组合名称';
comment on column osoadata.t_comb_request_record.balance is '金额';
comment on column osoadata.t_comb_request_record.fund_account is '资产账户';
comment on column osoadata.t_comb_request_record.branch_no is '分支机构';
comment on column osoadata.t_comb_request_record.sys_no is '系统节点编号';
comment on column osoadata.t_comb_request_record.op_station is '站点地址';
comment on column osoadata.t_comb_request_record.resp is '操作结果';
comment on column osoadata.t_comb_request_record.oper_code is '操作编码：1签约 2 追加投资 3 减少投资 4 解约 5 撤单';
comment on column osoadata.t_comb_request_record.oper_desc is '操作说明';
comment on column osoadata.t_comb_request_record.oper_ip is '执行操作的服务器ip';
comment on column osoadata.t_comb_request_record.create_date is '创建时间';
comment on column osoadata.t_comb_request_record.update_date is '更新时间';
comment on column osoadata.t_comb_request_record.redeem_ratio is '赎回比例';
comment on column osoadata.t_comb_request_record.trace_id is '链路id';
comment on column osoadata.t_comb_request_record.orig_comb_request_no is '原组合买入申请编号';
comment on column osoadata.t_comb_request_record.comb_request_no is '组合申请编号';
comment on column osoadata.t_comb_request_record.error_no is '错误编号';
comment on column osoadata.t_comb_request_record.error_info is '错误信息';
comment on column osoadata.t_comb_request_record.create_month is '月份';

create sequence trade.seq_comb_request_record
start with 1
nomaxvalue
increment by 1
nocycle
cache 20
order;


/**
 oceanbase 建表语句
 */
CREATE TABLE trade.t_comb_request_record (
	id INT(16) UNSIGNED NOT NULL,
	client_id VARCHAR(20) NOT NULL COMMENT '客户id',
	client_name VARCHAR(60) COMMENT '客户名称',
	mobile VARCHAR(20) COMMENT '激活手机号',
	comb_code VARCHAR(20) NOT NULL COMMENT '组合代码',
	comb_name VARCHAR(100) NOT NULL COMMENT '组合名称',
	balance DECIMAL(19,2) COMMENT '金额',
	fund_account VARCHAR(20) NOT NULL COMMENT '资产账户',
	branch_no VARCHAR(20) COMMENT '分支机构',
	sys_no VARCHAR(20) COMMENT '系统节点编号',
	op_station VARCHAR(255) COMMENT '站点地址',
	resp VARCHAR(1000) NOT NULL COMMENT '操作结果',
	oper_code VARCHAR(2) NOT NULL COMMENT '操作编码：1签约 2 追加投资 3 减少投资 4 解约 5 撤单',
	oper_desc VARCHAR(20) NOT NULL COMMENT '操作说明',
	oper_ip VARCHAR(20),
	create_date DATE NOT NULL,
	update_date DATE NOT NULL,
	create_month INT(8) NOT NULL COMMENT '月份',
	redeem_ratio DECIMAL(10,8) COMMENT '赎回比例',
	trace_id VARCHAR(100),
	orig_comb_request_no VARCHAR(255) COMMENT '原组合买入申请编号',
	comb_request_no VARCHAR(255) COMMENT '组合申请编号',
	error_no VARCHAR(20),
	error_info VARCHAR(1000),
	CONSTRAINT `PRIMARY` PRIMARY KEY (id)
);

