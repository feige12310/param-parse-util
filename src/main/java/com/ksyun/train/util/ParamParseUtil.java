package com.ksyun.train.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamParseUtil {

    public static <T> T parse(Class<T> clazz, String filePath) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {

        try {
            checkFilePath(filePath);
            // 如果路径有效，可以继续进行下一步操作
            File file = new File(filePath);
            if (file.exists()) {
                Yaml yaml = new Yaml(new Constructor(Map.class));
                FileInputStream inputStream = new FileInputStream(filePath);
                Map<String, Object> yamlData = (Map<String, Object>) yaml.load(inputStream);

                T obj= (T) setValue(clazz.getName(),yamlData);
                return obj;
            }else {
                System.out.println("文件不存在：" + file.getAbsolutePath());
            }
        } catch (InvalidFilePathException e) {
            System.err.println("无效的文件路径: " + e.getMessage());
        }
        T obj = clazz.getDeclaredConstructor().newInstance();
        return obj;
    }

    private static Object setValue(String className, Map<String, Object> yamlData) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

        Class<?> clazz = Class.forName(className);
        Object obj = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);
            String fieldName = field.getName();
            String yamlName = upperFirstLetter(fieldName);
            Object value = yamlData.get(yamlName);

            if(field.isAnnotationPresent(SkipMappingValueAnnotation.class) || !yamlData.containsKey(yamlName))
                continue;

            if (value == null || "NULL".equalsIgnoreCase(String.valueOf(value)) ) {
                field.set(obj, getDefaultValue(field.getType()));
            } else {
                if (field.getType().isAssignableFrom(List.class)) {
                    List<?> list = (List<?>) value;
                    List<Object> objectList = new ArrayList<>();
                    for (Object item : list) {
                        if (item instanceof Map) {
                            Type type= field.getGenericType();
                            Type acc=null;
                            if(type instanceof ParameterizedType) {
                                ParameterizedType parameterizedType = (ParameterizedType) type;
                                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                                for(Type t:actualTypeArguments) {
                                    acc = t;
                                }
                            }
                            Object listItem = setValue(acc.getTypeName(), (Map<String, Object>) item);
                            objectList.add(listItem);
                        } else if (isBaseType(item.getClass())) {
                            objectList.add(item);
                        } else if (item instanceof List) {
                            Object listItem = setValue(List.class.getName(), (Map<String, Object>) item);
                            objectList.add(listItem);
                        }
                    }
                    field.set(obj, objectList);
                } else if(isBaseType(field.getType())) {
                    setBaseValue(obj,field,value);
                }else  {
                    setMapValue(obj,field,yamlName,yamlData);
                }
            }
        }
        return obj;
    }

    private static <T> void setBaseValue(T obj,Field field, Object value) throws IllegalAccessException {
        field.set(obj, convertValue(field.getType(), value));
    }

    private static <T> void setMapValue(T obj,Field field, String yamlName,Map<String, Object> yamlData) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        field.set(obj, setValue(field.getType().getName(),(Map<String, Object>) yamlData.get(yamlName)));
    }

    private static boolean isBaseType(Class<?> clazz) {
        if (clazz.isPrimitive()|| isWrapClass(clazz)) {
            return true;
        }
        if (clazz.equals(String.class)|| clazz.equals(BigDecimal.class)) {
            return true;
        }
        return false;
    }

    private static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    private static Object convertValue(Class<?> type, Object value) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value.toString());
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value.toString());
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value.toString());
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value.toString());
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value.toString());
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value.toString());
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(value.toString());
        } else if (type == String.class) {
            return value.toString();
        } else if (type == char.class || type == Character.class) {
            return value.toString().charAt(0);
        } else if (type == BigDecimal.class) {
            return new BigDecimal(value.toString());
        } else {
            return value; // 处理自定义对象的情况
        }
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        } else if (type == byte.class) {
            return (byte) 0;
        } else if (type == short.class) {
            return (short) 0;
        } else if (type == int.class) {
            return 0;
        } else if (type == long.class) {
            return 0L;
        } else if (type == float.class) {
            return 0.0f;
        } else if (type == double.class) {
            return 0.0d;
        } else if (type == char.class) {
            return '\u0000';
        } else if (type == BigDecimal.class) {
            return BigDecimal.ZERO;
        } else {
            return null;
        }
    }

    private static String upperFirstLetter(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void checkFilePath(String filePath) throws InvalidFilePathException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new InvalidFilePathException("文件路径不能为空或仅包含空格。");
        }

        File file = new File(filePath);
        try {
            // 尝试获取文件的绝对路径，以验证路径格式是否正确
            file.getCanonicalPath();
        } catch (IOException e) {
            throw new InvalidFilePathException("文件路径格式无效: " + filePath);
        }
    }

//    private static Map<String, Object> readYamlFile(String filePath) throws IOException {
//        Map<String, Object> yamlData = new HashMap<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            String currentKey = null;
//            List<String> list = null;
//
//            while ((line = br.readLine()) != null) {
//                line = line.trim();
//                if (line.isEmpty() || line.startsWith("#")) {
//                    continue;
//                }
//
//                if (line.startsWith("- ")) {
//                    if (currentKey != null && list != null) {
//                        list.add(line.substring(2).trim());
//                    }
//                } else {
//                    int colonIndex = line.indexOf(":");
//                    if (colonIndex != -1) {
//                        currentKey = line.substring(0, colonIndex).trim();
//                        String value = line.substring(colonIndex + 1).trim();
//
//                        if (Character.isLowerCase(currentKey.charAt(0))) {
//                            continue; // 忽略小写开头的key
//                        }
//
//                        if (value.isEmpty()) {
//                            list = new ArrayList<>();
//                            yamlData.put(currentKey, list);
//                        } else {
//                            yamlData.put(currentKey, "NULL".equalsIgnoreCase(value) ? null : value);
//                        }
//                    }
//                }
//            }
//        }
//        return yamlData;
//    }
}
// 自定义异常类
class InvalidFilePathException extends Exception {
    public InvalidFilePathException(String message) {
        super(message);
    }
}