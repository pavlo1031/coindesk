set mode MySQL;

-- 幣別資料
DROP TABLE IF EXISTS coin_type;
CREATE TABLE coin_type (
	code char(4) NOT NULL PRIMARY KEY,
	symbol char(15),
	rate varchar(20),
	description varchar(100),
	description_chinese varchar(10),
	rate_float decimal(10,4),
	update_datetime datetime default now()
);
