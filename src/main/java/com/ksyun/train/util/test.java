package com.ksyun.train.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

    public static <T> T parse(Class<T> clazz, String filePath) throws IOException, IllegalAccessException, InstantiationException {

        Yaml yaml = new Yaml(new Constructor(Map.class));
        FileInputStream inputStream = new FileInputStream(filePath);
        Map<String, Object> yamlData = (Map<String, Object>) yaml.load(inputStream);

        T obj=setValue(clazz,yamlData);
        return obj;
    }

    private static <T> T setValue(Class<T> clazz, Map<String, Object> yamlData) throws InstantiationException, IllegalAccessException {

        T obj = clazz.newInstance();
        System.out.println(obj.toString());
        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);
            String fieldName = field.getName();
            String yamlName = upperFirstLetter(fieldName);
            Object value = yamlData.get(yamlName);

            if(field.isAnnotationPresent(SkipMappingValueAnnotation.class) || !yamlData.containsKey(yamlName))
                continue;
            System.out.print(yamlName+":\t"+String.valueOf(value)+"\t");
            if (value == null || "NULL".equalsIgnoreCase(String.valueOf(value)) ) {
                field.set(obj, getDefaultValue(field.getType()));
                System.out.println("moren\t" + obj.toString());
            } else {
                if (field.getType().isAssignableFrom(List.class)) {
                    field.set(obj, value);
                    System.out.println("liebiao \t" + obj.toString());
                } else if(field.getType().isPrimitive()||field.getType().equals(String.class)||field.getType().equals(BigDecimal.class)) {
                    field.set(obj, convertValue(field.getType(), value));
                    System.out.println("jichu \t" + obj.toString());
                } else {
                    field.set(obj, setValue(field.getType(),(Map<String, Object>) yamlData.get(yamlName)));
                    System.out.println("qiantao\t" + obj.toString());
                }
            }
        }

        return obj;
    }
    private static Map<String, Object> readYamlFile(String filePath) throws IOException {
        Map<String, Object> yamlData = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentKey = null;
            List<String> list = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("- ")) {
                    if (currentKey != null && list != null) {
                        list.add(line.substring(2).trim());
                    }
                } else {
                    int colonIndex = line.indexOf(":");
                    if (colonIndex != -1) {
                        currentKey = line.substring(0, colonIndex).trim();
                        String value = line.substring(colonIndex + 1).trim();

                        if (Character.isLowerCase(currentKey.charAt(0))) {
                            continue; // 忽略小写开头的key
                        }

                        if (value.isEmpty()) {
                            list = new ArrayList<>();
                            yamlData.put(currentKey, list);
                        } else {
                            yamlData.put(currentKey, "NULL".equalsIgnoreCase(value) ? null : value);
                        }
                    }
                }
            }
        }
        return yamlData;
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
}