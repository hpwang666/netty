package com.wwp.mapper;

import com.wwp.entity.YlcUserOrder;
import jdk.nashorn.internal.objects.annotations.Function;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface YlcUserOrderMapper {

    @Insert("insert into  ylc_user_order set id = #{id},user_id = #{userId},order_num = #{orderNum}")
    void add(YlcUserOrder ylcUserOrder);

    @Select("select user_id from ylc_user_order where order_num = #{orderNum}")
    String queryUserIdByOrderNum(@Param("orderNum") String orderNum);
}
