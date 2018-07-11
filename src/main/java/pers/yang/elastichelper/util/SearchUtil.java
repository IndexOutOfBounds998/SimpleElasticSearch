package pers.yang.elastichelper.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import pers.yang.elastichelper.annotations.Document;
import pers.yang.elastichelper.annotations.Field;
import pers.yang.elastichelper.annotations.ID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.*;
import pers.yang.elastichelper.common.Constant;
import pers.yang.elastichelper.common.ParamsParseException;
import org.apache.log4j.Logger;

import org.elasticsearch.search.sort.SortOrder;

/**
 * ElasticSearch相关的工具类，主要通过反射技术进行相关操作并获取相关类信息。 功能包括： 获取实体类的索引名称、获取实体类的类型名称、将ES实体类对象转换成Json对象、将Map对象转换为ES实体类对象、
 * 扫描指定包下面的ES实体类、获取建立索引所需的Setting信息、获取指定实体类相对应的mapping信息、 以及解析es的查询参数
 */
public class SearchUtil {
    /**
     * @Author yang
     * @Date 2018/7/11 18:22
     * @Description 过滤数据
     */
    private static final List<String> words = new ArrayList<String>() {{
        "Integer".
                toLowerCase();
        "Float".

                toLowerCase();
        "Double".

                toLowerCase();
        "Boolean".

                toLowerCase();
        "BigDecimal".

                toLowerCase();
        "Date".

                toLowerCase();
    }};
    private static Logger LOG = Logger.getLogger(SearchUtil.class);

    /* 获取指定实体类的索引名称 */
    public static <T> String getIndexName(T model) {
        return getIndexName(model.getClass());
    }

    /* 获取指定实体类的索引名称 */
    public static String getIndexName(Class clazz) {
        return ((Document) clazz.getAnnotation(Document.class)).index();
    }

    /* 获取指定实体类的类型名称 */
    public static <T> String getTypeName(T model) {
        return getTypeName(model.getClass());
    }

    /* 获取指定实体类的类型名称 */
    public static String getTypeName(Class clazz) {
        return ((Document) clazz.getAnnotation(Document.class)).type();
    }

    /* 判别一个类是否是Document（elasticsearch实体类） */
    public static boolean isDocument(Class clazz) {
        return clazz.isAnnotationPresent(Document.class);
        // return getIndexName(clazz) == null ? false : true;
    }

    /* 获取指定实体类所在索引的number_of_shards值 */
    public static short getShards(Class clazz) {
        return ((Document) clazz.getAnnotation(Document.class)).shards();
    }

    /* 获取指定实体类所在索引的number_of_replicas值 */
    public static short getReplicas(Class clazz) {
        return ((Document) clazz.getAnnotation(Document.class)).replicas();
    }

    /* 获取注解ID标志的字段名称 */
    public static String getidName(Class clazz) {
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        String id = null;
        outer:
        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(ID.class)) {
                id = field.getName();
                break outer;
            }
        }
        return id;
    }

    /* 获取注解ID标志的字段对应的值 */
    public static <T> Object getidValue(T model) {
        Class clazz = model.getClass();
        String idName = getidName(clazz);
        Object idValue = null;
        String firstLetter = idName.substring(0, 1).toUpperCase();
        String methodName = "get" + firstLetter + idName.substring(1);
        Method method = null;
        try {
            method = clazz.getMethod(methodName);
            idValue = method.invoke(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idValue;
    }

    /* 将实体类对象转换为Json字符串 */
    public static <T> String ModelToJson(T model) {
        // 直接调用fastjson 转成json字符串
        return JSON.toJSONString(model, true);
    }

    /**
     * 将传入的value字符串对象转换为type对应的具体子类类型对象（返回时会向上造型为Object对象） 注：只接受String及基本类型及对应包装类的转换
     */
    private static Object convert(Class type, String value) {
        if (type.equals(String.class)) {
            return value;
        }
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return Integer.parseInt(value);
        }
        if (type.equals(Short.class) || type.equals(short.class)) {
            if (value == null) {
                return 0;
            }
            return Short.parseShort(value);
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            if (value == null) {
                return 0;
            }
            return Integer.parseInt(value);
        }
        if (type.equals(Float.class) || type.equals(float.class)) {
            if (value == null) {
                return 0f;
            }
            return Float.parseFloat(value);
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            if (value == null) {
                return 0.0;
            }
            return Double.parseDouble(value);
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            if (value == null) {
                return 0l;
            }
            return Long.parseLong(value);
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            if (value == null) {
                return false;
            }
            return Boolean.parseBoolean(value);
        }
        return null;

    }

    /* 将map对象转换为对应的实体类对象 */
    public static <K, V, T> T MapToModel(Map<K, V> map, Class<T> clazz) {
        Method[] methods = clazz.getMethods();
        // Set<Method> methodsSet = new HashSet<Method>();
        Map<String, Method> methodsMap = new HashMap<String, Method>();
        for (Method method : methods) {
            // 添加所有Setter方法
            if (method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
                // methodsSet.add(method);
                methodsMap.put(method.getName(), method);
            }
        }
        if (map == null)
            return null;
        T model = null;
        try {
            model = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (K key : map.keySet()) {
            // System.out.println(key + "：" + map.get(key));
            String field = (String) key;
            // ES中通过client API获得到的结果map,所有的value都是String类型
            // （虽然是Object,但具体的子类类型仍未String,算作ES java client API的一个bug）
            Object value = map.get(key);
            // 拼装Setter方法名
            String firstLetter = field.substring(0, 1).toUpperCase();
            String methodName = "set" + firstLetter + field.substring(1);
            // System.out.println(methodName);
            try {
                // Method method = clazz.getDeclaredMethod(methodName,String.class);
                // method.invoke(model,value);
                Method method = methodsMap.get(methodName);
                if (method == null) {
                    throw new Exception("没有" + method.getName() + "方法，请检查相应实体类(可能的问题：ES中新增了字段，但在实体类中没有定义)！");
                }
                Class parameterType = method.getParameterTypes()[0];// 获取Setter的参数类型
                // 根据参数类型对value(String类型)进行类型转换（转换为具体的子类类型）
                Object value2 = convert(parameterType, value.toString());
                // 执行invoke方法，传入的参数类型必须匹配（第一个参数），同时传入的值value（第二个参数）具体的子类类型必须匹配
                // value可以进行向上造型操作，只要保证子类类型一致即可
                method.invoke(model, convert(parameterType, value2.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    /* 获得指定实体类中的Type注解中的Init参数值 */
    public static boolean getInitValue(Class<?> modelClazz) {
        return ((Document) modelClazz.getAnnotation(Document.class)).init();
    }

    /**
     * @Author yang
     * @Date 2018/7/11 18:23
     * @Description 获取指定类的mapping, 并组装成json字符串
     * 自动扫描 实体类
     */
    public static String getMapping(Class clazz) {
        if (!getInitValue(clazz))
            return null;
        JSONObject rootJson = new JSONObject();
        JSONObject typeJson = new JSONObject();
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName(clazz);
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        String fieldsStr = "[";
        JSONObject propertiesJson = new JSONObject();
        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(Field.class)) {
                JSONObject fieldJson = new JSONObject();
                fieldsStr += field.getName() + ",";
                Field ann = field.getAnnotation(Field.class);
                Object type = ann.type().name().toLowerCase();
                String index = ann.index().name();
                String stored = ann.stored() + "";
                String analyzer = ann.analyzer();
                String format = ann.format();

                boolean fields1 = ann.fields();
                // type = "string", index = "analyzed", stored = "true", analyzer = "standard"
                if (type.equals("Auto")) {// 如果是Auto类型则跳过，不进行mapping设置
                    continue;
                }
                fieldJson.put("type", type);
                // 是否多字段
                if (fields1) {
                    String[] fieldsName = ann.fieldsName();
                    String[] fieldsAnalyzer = ann.fieldsAnalyzer();
                    if (fieldsName.length > 0) {
                        Map<String, Object> root = new HashMap<>();
                        for (int i = 0; i < fieldsName.length; i++) {
                            Map<String, String> tree = new HashMap<>();
                            tree.put("type", "text");
                            // "index": "analyzed",
                            tree.put("index", "analyzed");
                            tree.put("analyzer", fieldsAnalyzer[i]);
                            root.put(fieldsName[i], tree);
                        }
                        fieldJson.put("fields", JSONObject.parseObject(JSON.toJSONString(root), root.getClass()));
                    }

                } else {
                    // int dou bigd 类的不能添加分词
                    if (!words.contains(type)) {
                        fieldJson.put("analyzer", analyzer);
                    }
                    // 是时间类型的字段 默认加上哦一个format
                    if (type.equals("date")) {
                        fieldJson.put("format", format);
                    } else {
                        fieldJson.put("store", stored);
                        fieldJson.put("index", index);
                    }
                }

                propertiesJson.put(field.getName(), fieldJson);
            }
        }
        typeJson.put("properties", propertiesJson);
        rootJson.put(typeName, typeJson);
        fieldsStr = fieldsStr.substring(0, fieldsStr.length() - 1) + "]";
        LOG.info("扫描实体类：" + clazz + ",发现索引：" + indexName + ",发现类型：" + typeName + ",发现Field有效字段为：" + fieldsStr);
        LOG.info("扫描类" + clazz.getName() + "完成，生成的mapping如下：\n" + rootJson.toJSONString());
        return rootJson.toJSONString();
    }

    /* 读取setting配置文件 */
    public static String getSettings(Class clazz) {
        // 获得实体类对应的index对应的settings配置文件
        Annotation[] annotationArr = clazz.getAnnotations();
        String settingsFile = null;
        for (Annotation annotation : annotationArr) {
            if (annotation instanceof Document) {
                settingsFile = ((Document) annotation).settings();
                break;
            }
        }
        // 是否使用指定配置文件来创建索引
        boolean crateIndexWithSettings = "".endsWith(settingsFile) ? false : true;
        if (!crateIndexWithSettings) {
            return null;
        }
        LOG.info("读取settings文件：classpath/" + settingsFile + " ....");
        InputStream stream = ClassLoader.getSystemResourceAsStream(settingsFile);
        // Preconditions.checkNotNull(stream,"settings.yml文件不存在");
        if (stream == null) {
            throw new RuntimeException("classpath/" + settingsFile + "文件不存在");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuffer settings = new StringBuffer();
        try {
            while ((line = br.readLine()) != null) {// 一次输出文本中的一行内容
                // System.out.println(line);
                if (!line.startsWith("#"))
                    settings.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings.toString();
    }

    /* 生成指定的Setting信息，暂时不用 */
    public static String getSettings() {
        JSONObject root = new JSONObject();
        JSONObject settings = new JSONObject();
        settings.put("number_of_shards", "3");
        settings.put("number_of_replicas", "1");
        JSONObject analysis = new JSONObject();
        JSONObject analyzer = new JSONObject();
        JSONObject pinyin_analyzer = new JSONObject();
        pinyin_analyzer.put("type", "pattern");
        pinyin_analyzer.put("pattern", "\\w");
        pinyin_analyzer.put("alias", new String[]{"pinyin"});
        JSONObject douhao_analyzer = new JSONObject();
        douhao_analyzer.put("type", "pattern");
        douhao_analyzer.put("pattern", ",");
        douhao_analyzer.put("alias", new String[]{"douhao"});
        JSONObject fenhao_analyzer = new JSONObject();
        fenhao_analyzer.put("type", "pattern");
        fenhao_analyzer.put("pattern", ";");
        fenhao_analyzer.put("alias", new String[]{"fenhao"});
        JSONObject ik_analyzer = new JSONObject();
        ik_analyzer.put("type", "org.elasticsearch.index.analysis.IkAnalyzerProvider");
        ik_analyzer.put("alias", new String[]{"ik_analyzer"});
        analyzer.put("pinyin_analyzer", pinyin_analyzer);
        analyzer.put("douhao_analyzer", douhao_analyzer);
        analyzer.put("fenhao_analyzer", fenhao_analyzer);
        analyzer.put("ik", ik_analyzer);
        analysis.put("analyzer", analyzer);
        settings.put("analysis", analysis);
        root.put("settings", settings);
        return root.toJSONString();
    }

}
