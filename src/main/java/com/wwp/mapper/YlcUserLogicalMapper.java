package com.wwp.mapper;

import com.wwp.entity.YlcUserLogical;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface YlcUserLogicalMapper {

    void add(YlcUserLogical ylcUserLogical);

    void updateUserAmount(String userId, BigDecimal amount);

    BigDecimal queryUserAmount(String userId);

    @Select("select physical_num from ylc_user_logical where user_id = #{userId}")
    String queryUserPhysicalNum(String userId);

    @Select("select * from ylc_user_logical where user_id = #{userId}")
    YlcUserLogical queryByUserId(String userId);
}
