package org.apache.ibatis.session.threadinfo;

import java.util.Iterator;
import java.util.Map;

/**
 * 线程级别变量
 *
 * @author
 * @create 2018-02-02 16:39
 **/
public class InvocationInfoProxy {

    private static final ThreadLocal<InvocationInfo> threadLocal = new ThreadLocal() {
        @Override
        protected InvocationInfo initialValue() {
            return new InvocationInfo();
        }
    };

    public InvocationInfoProxy() {
    }

    public static void reset() {
        threadLocal.remove();
    }


    public static String getCorpId() {
        return ((InvocationInfo) threadLocal.get()).corpId;
    }

    public static void setCorpId(String corpId) {
        ((InvocationInfo) threadLocal.get()).corpId = corpId;
    }

    public static String getTenantId() {
        return ((InvocationInfo) threadLocal.get()).tenantId;
    }

    public static void setTenantId(String tenantId) {
        ((InvocationInfo) threadLocal.get()).tenantId = tenantId;
    }

    public static String getUserId() {
        return ((InvocationInfo) threadLocal.get()).userId;
    }

    public static void setUserId(String userId) {
        ((InvocationInfo) threadLocal.get()).userId = userId;
    }

    public static String getEncryToken() {
        return ((InvocationInfo) threadLocal.get()).encryToken;
    }

    public static void setEncryToken(String encryToken) {

        ((InvocationInfo) threadLocal.get()).encryToken = encryToken;
    }

    public static String getUserName() {
        return ((InvocationInfo) threadLocal.get()).userName;
    }

    public static void setUserName(String userName) {
        ((InvocationInfo) threadLocal.get()).userName = userName;
    }

    public static Iterator<Map.Entry<String, String>> getSummry() {
        return ((InvocationInfo) threadLocal.get()).getSummry();
    }


}
