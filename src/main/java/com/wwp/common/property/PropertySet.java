package com.wwp.common.property;

import com.wwp.common.annotation.Id;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PropertySet {

    private HashMap<String,UUIDGenerator> propertys = new HashMap<String,UUIDGenerator>();

    private Class<?> entity;

    @SuppressWarnings("unused")
    private PropertySet() {

    }

    public PropertySet(Class<?> entity) {
        this.entity = entity;
        this.build();
    }

    public HashMap<String,UUIDGenerator> getPropertys() {
        return propertys;
    }

    public void setPropertys(HashMap<String,UUIDGenerator> propertys) {
        this.propertys = propertys;
    }

    public PropertySet build() {

        Class clazz = entity;

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            if ("serialVersionUID".equals(field.getName()) )
                continue;
            field.setAccessible(true);
            PropertyDescriptor propertyDescriptor = null;
            //没有setter 方法  这儿会报错
            try {
                propertyDescriptor = new PropertyDescriptor(field.getName(), entity);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }

            if (propertyDescriptor == null)
                continue;
            // 获取类的get方法
            Method method = propertyDescriptor.getReadMethod();
            if (method == null) {
                continue;
            }

            if (field.isAnnotationPresent(Id.class)) {
                Id id = field.getAnnotation(Id.class);
                if (null == id.strategy()) {
                    continue;
                }
                Class<?> strategy = id.strategy();
                Object newInstance = null;
                try {
                    newInstance = strategy.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!(newInstance instanceof UUIDGenerator)) {
                    continue;
                }
                System.out.println("uuid");
                propertys.put(field.getName(), (UUIDGenerator)newInstance);
            }
            else if (false) {//method.isAnnotationPresent(Id.class) //注解在方法上
                Id id = method.getAnnotation(Id.class);
                if (id.strategy() == null) {
                    continue;
                }
                Class<?> generator = id.strategy();
                Object object = null;
                try {
                    object = generator.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!(object instanceof UUIDGenerator)) {
                    continue;
                }

                propertys.put(field.getName(), (UUIDGenerator)object);
                break;
            } else if (false) {//String.class.equals(field.getType()) && "id".equalsIgnoreCase(field.getName())//强制要求注解
                UUIDGenerator uuidGenerator = new UUIDGenerator();

                propertys.put(field.getName(), (UUIDGenerator)uuidGenerator);
            }
        }
        return this;
    }
}