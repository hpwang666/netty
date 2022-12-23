package com.wwp.model;


public enum YlcMsgType {
    DO_NOTHING(0X00),
    AUTH(0X01),
    AUTH_ACK(0X02),
    HEART(0x03),
    HEART_ACK(0x04),
    MODEL_VERIFY(0x05),
    MODEL_VERIFY_ACK(0x06),
    GET_MODEL(0X09),
    GET_MODEL_ACK(0X0A),
    RT_STATUS(0X12),
    UP_RT_STATUS(0X13),
    CHARGE_CTX(0X15),
    UP_CONFIG(0X17),
    UP_CHARGE_END(0X19),
    ERROR(0X1B),
    BMS_STOP(0X1D),
    CHARGE_STOP(0X21),
    BMS_REQ(0X23),
    BMS_INFO(0X25),
    UP_CHARGE_REQ(0X31),
    UP_CHARGE_ACK(0X32),
    REMOTE_ON_ACK(0X33),
    REMOTE_ON(0X34),
    REMOTE_OFF_ACK(0X35),
    REMOTE_OFF(0X36),
    RECORD(0X3B),
    RECORD_ACK(0X40),
    REMAIN_UPDATE_ACK(0X41),
    REMAIN_UPDATE(0X42),
    CARD_UPDATE_ACK(0X43),
    CARD_UPDATE(0X44),
    CARD_CLEAN_ACK(0X45),
    CARD_CLEAN(0X46),
    CARD_READ_ACK(0X47),
    CARD_READ(0X48),
    DOWN_CFG_ACK(0X51),
    DOWN_CFG(0X52),
    TIME_CFG_ACK(0X55),
    TIME_CFG(0X56),
    DOWN_MODEL_ACK(0X57),
    DOWN_MODEL(0X58),
    UP_LOCK(0X61),
    DOWN_LOCK(0X62),
    UP_DATA(0X63),
    REMOTE_REBOOT_ACK(0X91),
    REMOTE_REBOOT(0X92),
    REMOTE_UPDATE_ACK(0X93),
    REMOTE_UPDATE(0X94);


    private final int value;

    private YlcMsgType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public static YlcMsgType valueOf(int type) {
        YlcMsgType[] arr$ = values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            YlcMsgType t = arr$[i$];
            if (t.value == type) {
                return t;
            }
        }

        throw new IllegalArgumentException("unknown message type: " + type);
    }
}
