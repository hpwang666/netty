use `test`;



SET NAMES utf8;

DROP TABLE IF EXISTS `ylc_charger`;
CREATE TABLE `ylc_charger` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `serial_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备序列号',
  `depart_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属车场',
  `type` tinyint(1) NULL DEFAULT 0 COMMENT '充电桩类型 ',
  `plugs` tinyint(1) NULL DEFAULT 0 COMMENT '充电枪数量 ',
  `model_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计费编码模型',
  `plug_status` tinyint(1)  NULL DEFAULT 0 COMMENT '0x00：离线   0x01：故障  0x02：空闲  0x03：充电',
  `plug_homing` tinyint(1)  NULL DEFAULT 0 COMMENT '枪是否归位 0--否  1--是  2--未知',
  `slot_in` tinyint(1) NULL DEFAULT 0 COMMENT '是否插枪 0--否  1--是 ',
  `error_code` smallint NULL DEFAULT 0 COMMENT '故障代码',
  `update_time` datetime NULL DEFAULT NULL COMMENT '心跳时间',
  `del_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除状态  0正常，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

INSERT INTO `ylc_charger` VALUES ('1260185893546840066', '32010600213533', '1223', 1, 1, '0001', 1, 1,1,1,NULL,0);


DROP TABLE IF EXISTS `ylc_fee_model`;
CREATE TABLE `ylc_fee_model` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `model_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型编码',
  `fee0` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '尖费率',
  `fee1` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '峰费率',
  `fee2` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '平费率',
  `fee3` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '谷费率',
  `loss_rate` tinyint(1)  NULL DEFAULT NULL COMMENT '计损比率 ',
  `fees_by_model` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '48段费率表',
   PRIMARY KEY (`id`) USING BTREE,
  KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
INSERT INTO `ylc_fee_model` VALUES ('1260185893546841122', '1324', '0000C35000007530', '0000C35000007530', '0000C35000007530', '0000C35000007530', 0, '000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000');



DROP TABLE IF EXISTS `ylc_logical_physical`;
CREATE TABLE `ylc_logical_physical` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `logical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `physical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_logical_num` (`logical_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `ylc_order`;
CREATE TABLE `ylc_order` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易流水号',
  `serial_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '桩号',
  `plug_no` tinyint(1) NULL DEFAULT NULL COMMENT '枪号',
  `start_amount` int NULL DEFAULT NULL COMMENT '下发金额--点2',
  `total_kwh` int NULL DEFAULT NULL COMMENT '总用电量--点4',
  `total_cost` int  NULL DEFAULT NULL COMMENT '消费金额--点4',
  `order_time` datetime NULL DEFAULT NULL COMMENT '交易日期',
  `physical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物理卡号',
  `stop_type` tinyint(1) NULL DEFAULT NULL COMMENT '停止原因',
  `settle_flag` tinyint(1) NULL DEFAULT NULL COMMENT '结算标志 0--未结算   1--结算',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_order_num` (`order_num`) USING BTREE,
   KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS `ylc_charger_status`;
CREATE TABLE `ylc_charger_status` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易流水号',
  `serial_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '桩号',
  `plug_no` tinyint(1) NULL DEFAULT NULL COMMENT '枪号',
  `voltage` int NULL DEFAULT NULL COMMENT '电压',
  `current` int NULL DEFAULT NULL COMMENT '电流',
  `charge_min`  int NULL DEFAULT NULL COMMENT '充电时长',
  `charge_kwh`  int NULL DEFAULT NULL COMMENT '充电度数-点4',
  `loss_kwh`    int NULL DEFAULT NULL COMMENT '计损度数-点4',
  `charge_cost`  int  NULL DEFAULT NULL COMMENT '已消费金额--点4',
  `update_time` datetime NULL DEFAULT NULL COMMENT '记录--更新日期',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS `ylc_user_order`;
CREATE TABLE `ylc_user_order` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易流水号',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `ylc_user_logical`;
CREATE TABLE `ylc_user_logical` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `logical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '逻辑卡号',
  `amount` bigint  NULL DEFAULT 0 COMMENT '账户金额--点2',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_logical_num` (`logical_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



