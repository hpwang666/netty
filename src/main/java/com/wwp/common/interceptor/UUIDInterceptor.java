package com.wwp.common.interceptor;

import com.wwp.common.property.PropertySet;
import com.wwp.common.property.UUIDGenerator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class UUIDInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {


        Object[] args = invocation.getArgs();
        //当用了@Param后，args[1]就会是MapperMethod.ParamMap，否则就是直接的参数实体
        if (args == null || args.length != 2 || !(args[0] instanceof MappedStatement) ) {
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (!SqlCommandType.INSERT.equals(sqlCommandType)) {
            System.out.println("no insert...");
            return invocation.proceed();
        }
        if((args[1] instanceof Map)){
            MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) args[1];
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                Object paramValue = entry.getValue();
                //所有的参数会以 Param1 Param2 .... 的形式展现

                //排除了对Map List String 的处理  只处理定义的实体
                if(entry.getKey().startsWith("param")&& paramValue.getClass().getName().startsWith("com.neo")){
                    System.out.println("key: "+ entry.getKey());
                    System.out.println("name: "+paramValue.getClass().getName());
                    setDefultProperty(paramValue);
                }
            }
        }
       else{//没有使用@Param
           if(args[1].getClass().getName().startsWith("com.wwp")) {
               System.out.println("name: "+args[1].getClass().getName());
               setDefultProperty(args[1]);
           }
       }
        return invocation.proceed();
    }

    public void setDefultProperty(Object obj) {

        PropertySet propertySet = new PropertySet(obj.getClass());

        HashMap<String, UUIDGenerator> propers = propertySet.getPropertys();
        if (propers == null || propers.isEmpty())
            return;
        for (Map.Entry <String, UUIDGenerator> pro : propers.entrySet()) {
            try {
                BeanUtils.setProperty(obj, pro.getKey(), pro.getValue().generator());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("init UUIDInterceptor");
    }

}
