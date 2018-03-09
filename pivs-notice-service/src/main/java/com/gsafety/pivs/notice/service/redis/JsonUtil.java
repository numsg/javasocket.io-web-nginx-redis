package com.gsafety.pivs.notice.service.redis;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by gaoqiang on 2016/5/12.
 */
public class JsonUtil {

    private static ObjectMapper mapper = null;

    private Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    static {
        mapper = new ObjectMapper();
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //允许key没有使用双引号的json
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        //零时区
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        mapper.setDateFormat(format);
    }

    /**
     * 将对象转换成JSON字符串
     *
     * @param obj 目标对象
     * @return 字符串 ，转换失败时返回null
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 将单个键值对转换成JSON字符串，用于返回只有一个键值对json时的便捷方法
     *
     * @param key   目标对象
     * @param value the value
     * @return 字符串 ，转换失败时返回null
     */
    public static String toJson(Object key, Object value) {
        Map<Object, Object> map = new HashMap<>();
        map.put(key, value);
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 转换JSON对象为Ext表单提交成功的方式<br />
     * ex.{"success":"false","data":{"name":"名称已存在"}}
     *
     * @param obj     错误信息，若用map，Key若为表单项的name，则会自动在对应表单项显示错误信息
     * @param success the success
     * @return the string
     */
    public static String toFormJson(Object obj, boolean success) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("data", obj);
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 转换分页查询结果
     *
     * @param list  结果集
     * @param total 记录数
     * @return the string
     */
    public static String writePageJson(Object list, Object total) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", total);
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String, JavaType)
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>
     *
     * @param <L>             the type parameter
     * @param <E>             the type parameter
     * @param jsonString      the json string
     * @param collectionClass the collection class
     * @param elementClass    the element class
     * @return 转换失败时返回 null
     */
    public static <L extends Collection<E>, E> L fromJson(String jsonString,
                                                          Class<L> collectionClass,
                                                          Class<E> elementClass) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            CollectionType type = mapper.getTypeFactory().constructCollectionType(collectionClass,
                    elementClass);
            return mapper.readValue(jsonString, type);
        } catch (Exception e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 当JSON里只含有Bean的部分属性，更新一个已存在Bean，只覆盖该部分的属性.
     *
     * @param jsonString the json string
     * @param object     the object
     */
    public static void update(String jsonString, Object object) {
        try {
            mapper.readerForUpdating(object).readValue(jsonString);
        } catch (IOException e) {
            (new JsonUtil()).logger.error(e.getMessage(),e);
        }
    }

    /**
     * 输出JSONP格式数据
     *
     * @param functionName the function name
     * @param object       the object
     * @return the string
     */
    public static String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }
}
