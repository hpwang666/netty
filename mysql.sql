use `test`;



SET NAMES utf8;


CREATE TABLE `ylc_charger` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `serial_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备序列号',
  `depart_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属车场',
  `type` tinyint(1)  NOT NULL COMMENT '充电桩类型 ',
  `plugs` tinyint(1)  NOT NULL COMMENT '充电枪数量 ',
  `model_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计费编码模型',
  `plug_status` tinyint(1)  NOT NULL COMMENT '0x00：离线   0x01：故障  0x02：空闲  0x03：充电',
  `plug_homing` tinyint(1)  DEFAULT 0 COMMENT '枪是否归位 0--否  1--是  2--未知',
  `slot_in` tinyint(1)  DEFAULT 0 COMMENT '是否插枪 0--否  1--是 ',
  `error_code` smallint DEFAULT 0 COMMENT '故障代码',
  `update_time` datetime DEFAULT NULL COMMENT '心跳时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除状态  0正常，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


CREATE TABLE `ylc_fee_model` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `model_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型编码',
  `fee0` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '尖费率',
  `fee1` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '峰费率',
  `fee2` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '平费率',
  `fee3` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '谷费率',
  `loss_rate` tinyint(1)  DEFAULT 0 COMMENT '计损比率 ',
  `fees_by_model` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '48段费率表',
   PRIMARY KEY (`id`) USING BTREE,
  KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE `ylc_logical_physical` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `logical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `physical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_logical_num` (`logical_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


CREATE TABLE `ylc_order` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易流水号',
  `serial_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '桩号',
  `plug_no` tinyint(1) NOT NULL COMMENT '枪号',
  `start_amount` bigint NOT NULL COMMENT '下发金额--点2',
  `total_kwh` bigint DEFAULT 0 COMMENT '总用电量--点4',
  `total_cost` bigint  DEFAULT 0 COMMENT '消费金额--点4',
  `order_time` datetime DEFAULT NULL COMMENT '交易日期',
  `physical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物理卡号',
  `stop_type` tinyint(1) DEFAULT 0 COMMENT '停止原因',
  `settle_flag` tinyint(1) DEFAULT 0 COMMENT '结算标志 0--未结算   1--结算',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_order_num` (`order_num`) USING BTREE,
   KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE `ylc_charger_status` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易流水号',
  `serial_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '桩号',
  `plug_no` tinyint(1) NOT NULL COMMENT '枪号',
  `voltage` int DEFAULT 0 COMMENT '电压',
  `current` int DEFAULT 0 COMMENT '电流',
  `charge_min`  int DEFAULT 0 COMMENT '充电时长',
  `charge_kwh`  bigint DEFAULT 0 COMMENT '充电度数-点4',
  `loss_kwh`    bigint DEFAULT 0 COMMENT '计损度数-点4',
  `charge_cost`  bigint  DEFAULT 0 COMMENT '已消费金额--点4',
  `update_time` datetime DEFAULT NULL COMMENT '记录--更新日期',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE `ylc_user_order` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `order_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '交易流水号',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_order_num` (`order_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

CREATE TABLE `ylc_user_logical` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `logical_num` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '逻辑卡号',
  `amount` bigint  DEFAULT 0 COMMENT '账户金额--点2',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `index_id` (`id`) USING BTREE,
   KEY `index_logical_card` (`logical_card`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



