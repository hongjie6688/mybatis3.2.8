/*
 *    Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.scripting.defaults;

import org.apache.ibatis.annotations.NeedEncry;
import org.apache.ibatis.encry.AESEncryUtil;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.threadinfo.InvocationInfoProxy;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */
public class DefaultParameterHandler implements ParameterHandler {

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private BoundSql boundSql;
    private Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public void setParameters(PreparedStatement ps) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Environment environment = configuration.getEnvironment();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    //    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) jdbcType = configuration.getJdbcTypeForNull();
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    boolean needEncry = mappedStatement.isEncryType();
                    if (needEncry) {
                        Class clazz = parameterObject.getClass();
                        try {
                            String needEncryValue = objectToString(value, needEncry);
                            if (needEncry) {
                                Field field = clazz.getDeclaredField(propertyName);
                                NeedEncry needEncryClass = field.getAnnotation(NeedEncry.class);
                                if (needEncryClass != null && value != null) {
                                    // TODO 做解密
                                    // 获取秘钥
                                    value = AESEncryUtil.encrypt(needEncryValue, InvocationInfoProxy.getEncryToken());
                                    typeHandler = new StringTypeHandler();
                                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                                } else {
                                    typeHandler.setParameter(ps, i + 1, value, jdbcType);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        typeHandler.setParameter(ps, i + 1, value, jdbcType);
                    }
                }
            }
        }
    }


    /**
     * object to string
     *
     * @param object
     * @return
     */

    private String objectToString(Object object, boolean flag) {
        if (object instanceof String) {
            return object.toString();
        } else if (object instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) object;
            bigDecimal = bigDecimal.setScale(8, BigDecimal.ROUND_HALF_UP);
            return bigDecimal.toString();
        } else if (object instanceof Long || object instanceof Integer || object instanceof Double
                || object instanceof Byte || object instanceof Float || object instanceof Short
                || object instanceof Boolean) {
            return object.toString();
        } else if (object.getClass().isPrimitive()) {
            System.out.println(object.getClass());
            return (String) object;
        } else if (object instanceof Date) {
            flag = false;
            return "";
        }
        return "";
    }

}
