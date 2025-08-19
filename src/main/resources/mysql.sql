create database feed_inventory;
use feed_inventory;
create table tbl_feed_basic_info(
                                    feed_key varchar(50),
                                    feed_name varchar(100),
                                    feed_type varchar(20),
                                    workstream varchar(20),
                                    source_app_name varchar(30),
                                    destination_app_name varchar(30),
                                    job_name varchar(100),
                                    source_contact varchar(200),
                                    destination_contact varchar(200),
                                    status varchar(10)
);

insert into tbl_feed_basic_info values('F_12_56_assumptions','assumptions_{yyyyMMdd}.gz'				,'gz','SECTIN'	,'OCEANA','AQUZ'		,'12345_ETL_assumptions_loading','oceana@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_12_56_marketshocs','marketshocs_{yyyyMMdd}.gz'				,'gz','SECTIN'	,'OCEANA','AQUZ'		,'12345_ETL_marketshocs_loading','oceana@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_12_56_unwind_schedule','unwind_schedule_{yyyyMMdd}.gz'		,'gz','CLND'	,'OCEANA','AQUZ'		,'12345_ETL_unwind_schedule_loading','oceana@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_33_56_collateral','collateral_{yyyyMMdd}.gz'					,'gz','CLND'	,'AMCD'	,'AQUZ'		,'12345_ETL_collateral_loading','amcd@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_44_56_balance_sheet','balance_sheet_{yyyyMMdd}.gz'			,'gz','DSFE'	,'SMCD'	,'AQUZ'		,'12345_ETL_balance_sheet_loading','smcd@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_44_56_POI_Pricing','POI_Pricing_{yyyyMMdd}.gz'				,'gz','SNN'		,'SMCD'	,'AQUZ'		,'12345_ETL_POI_Pricing_loading','smcd@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_44_56_business_users','business_users_{yyyyMMdd}.gz'			,'gz','TWE'		,'SMCD'	,'AQUZ'		,'12345_ETL_business_users_loading','smcd@email.com','aquaz@email.com','active');
insert into tbl_feed_basic_info values('F_56_90_benchmark','benchmark_{yyyyMMdd}.gz'					,'gz','SECTIN'	,'AQUZ','OLYMNUS'	,'12345_ETL_benchmark_loading','aquaz@email.com','olymnus@email.com','active');
insert into tbl_feed_basic_info values('F_56_90_MC_MASTER','MC_MASTER_{yyyyMMdd}.gz'					,'gz','TRQ'		,'AQUZ','OLYMNUS'	,'12345_ETL_MC_MASTER_loading','aquaz@email.com','olymnus@email.com','active');
insert into tbl_feed_basic_info values('F_56_90_assumptions_COMMON','assumptions_COMMON_{yyyyMMdd}.gz'	,'gz','DSFE'	,'AQUZ','OLYMNUS'	,'12345_ETL_assumptions_COMMON_loading','aquaz@email.com','olymnus@email.com','active');

SELECT * FROM tbl_feed_basic_info;