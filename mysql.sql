use `test`;



SET NAMES utf8;


CREATE TABLE `dev_charger` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `serial_num` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备序列号',
  `depart_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属车场',
  `type` tinyint(1)  NOT NULL COMMENT '充电桩类型 ',
  `plugs` tinyint(1)  NOT NULL COMMENT '充电枪数量 ',
  `model_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '计费编码模型',
  `plug_status` tinyint(1)  NOT NULL COMMENT '0x00：离线   0x01：故障  0x02：空闲  0x03：充电',
  `update_time` datetime DEFAULT NULL COMMENT '心跳时间',
  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除状态  0正常，1已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_serial_num` (`serial_num`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


CREATE TABLE `fee_model` (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `model_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型编码',
  `fee0` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '尖费率',
  `fee1` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '峰费率',
  `fee2` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '平费率',
  `fee3` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '谷费率',
  `loss_rate` tinyint(1)  NOT NULL COMMENT '计损比率 ',
  `fees_by_model` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '48段费率表',
   PRIMARY KEY (`id`) USING BTREE,
  KEY `index_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;



