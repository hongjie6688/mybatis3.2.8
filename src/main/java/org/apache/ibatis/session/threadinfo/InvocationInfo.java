package org.apache.ibatis.session.threadinfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 线程级别信息
 *
 * @author
 * @create 2018-02-02 16:40
 **/
public class InvocationInfo {
    String corpId;
    String tenantId;
    String userId;
    String encryToken;
    String userName;
    Map<String, String> parameters = new HashMap();

    InvocationInfo() {
    }

    public Iterator<Map.Entry<String, String>> getSummry() {
        HashMap map = new HashMap();
        map.putAll(this.parameters);
        map.put("corpId", this.corpId);
        map.put("tenantId", this.tenantId);
        map.put("userId", this.userId);
        map.put("encryToken", this.encryToken);
        map.put("userName", this.userName);
        return map.entrySet().iterator();
    }
}
