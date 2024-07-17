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

public class test {

    public static <T> T parse(Class<T> clazz, String filePath) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {

        Yaml yaml = new Yaml(new Constructor(Map.class));
        FileInputStream inputStream = new FileInputStream(filePath);
        Map<String, Object> yamlData = (Map<String, Object>) yaml.load(inputStream);

        T obj= (T) setValue(clazz.getName(),yamlData);
        return obj;
    }

    private static Object setValue(String className, Map<String, Object> yamlData) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        System.out.println(className+"classname66666");
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.getDeclaredConstructor().newInstance();
        System.out.println("clazz begin\t"+obj.toString());

        for (Field field : clazz.getDeclaredFields()) {

            field.setAccessible(true);
            String fieldName = field.getName();
            String yamlName = upperFirstLetter(fieldName);
            Object value = yamlData.get(yamlName);

            if(field.isAnnotationPresent(SkipMappingValueAnnotation.class) || !yamlData.containsKey(yamlName))
                continue;

            System.out.print(field.getType()+":\t"+String.valueOf(value)+"\t");

            if (value == null || "NULL".equalsIgnoreCase(String.valueOf(value)) ) {
                field.set(obj, getDefaultValue(field.getType()));
                System.out.println("moren\t" + obj.toString());
            } else {
                if (field.getType().isAssignableFrom(List.class)) {
//                    System.out.println("listtype\t"+field.getName()+field.getGenericType());
//                    setListValue(obj,field,value,yamlName,yamlData);
                    List<?> list = (List<?>) value;
                    List<Object> objectList = new ArrayList<>();
                    for (Object item : list) {
                        if (item instanceof Map) {
                            System.out.println("111111111111");
//                            String itemClassName = field.getGenericType().getTypeName().replaceAll("^.*<(.*)>$", "$1");
//                            String itemClassName = field.getGenericType().getTypeName();


                            Type type= field.getGenericType();
                            Type acc=null;
                            if(type instanceof ParameterizedType) {
                                ParameterizedType parameterizedType = (ParameterizedType) type;
                                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                                for(Type t:actualTypeArguments) {
                                    acc = t;
                                    System.out.println(t.getTypeName() + "aaaaa");
                                }
                            }else
                                System.out.println(type.getTypeName()+"sassaa");
                            System.out.println(acc+"cccccc");
                            Object listItem = setValue(acc.getTypeName(), (Map<String, Object>) item);
                            objectList.add(listItem);

                        } else if (item.getClass().isPrimitive()||item.getClass().equals(String.class)||item.getClass().equals(BigDecimal.class)) {
                            objectList.add(item);
                        } else if (item instanceof List) {
                            Object listItem = setValue(List.class.getName(), (Map<String, Object>) item);
                            objectList.add(listItem);
                        }


                    }
                    field.set(obj, objectList);




                } else if(field.getType().isPrimitive()||field.getType().equals(String.class)||field.getType().equals(BigDecimal.class)) {
                    setBaseValue(obj,field,value);
//                    field.set(obj, value);
//                    field.set(obj, convertValue(field.getType(), value));
//                    System.out.println("jichu \t" + obj.toString());
                }else  {
                    System.out.println("mmmmmmmmmmmmmmmmmm");
                    setMapValue(obj,field,yamlName,yamlData);
//                    field.set(obj, setValue(field.getType(),(Map<String, Object>) yamlData.get(yamlName)));
//                    System.out.println("qiantao\t" + obj.toString());
                }
            }
        }

        return obj;
    }

    //Pod{metadata=Metadata{generation=0, name='my-pod'}, container=[Container{name='nginx', command=[], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='123456'}], port=8306, imagePullPolicy='Always'}], cpu=null, memory=4.0, autoCreated=false, apiVersion='v1'}
    //Pod{metadata=Metadata{generation=0, name='my-pod'}, container=[Container{name='nginx', command=[/bin/bash, -c, sleep 20], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='123456'}], port=8306, imagePullPolicy='Always'}], cpu=null, memory=4.0, autoCreated=false, apiVersion='v1'}
    //Pod{metadata=Metadata{generation=0, name='my-pod'}, container=[Container{name='nginx', command=[/bin/bash, -c, sleep 20], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='33061'}], port=8306, imagePullPolicy='Always'}], cpu=null, memory=4.0, autoCreated=false, apiVersion='v1'}
    //Pod{metadata=Metadata{generation=0, name='my-pod'}, container=[Container{name='nginx', command=[/bin/bash, -c, sleep 20], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='123456'}], port=8306, imagePullPolicy='Always'}], cpu=null, memory=4.0, autoCreated=false, apiVersion='v1'}

    //Pod{metadata=Metadata{generation=0, name='my-pod'}, container=[Container{name='nginx', command=[/bin/bash, -c, sleep 20], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='123456'}], port=8306, imagePullPolicy='Always'}], cpu=6, memory=4.0, autoCreated=false, apiVersion='v1'}
    //Pod{metadata=Metadata{generation=0, name='my-pod'}, container=[Container{name='nginx', command=[/bin/bash, -c, sleep 20], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='123456'}], port=8306, imagePullPolicy='Always'}], cpu=null, memory=4.0, autoCreated=false, apiVersion='v1'}
    //Pod{metadata=null, container=[Container{name='nginx', command=[/bin/bash, -c, sleep 20], environment=null, port=8080, imagePullPolicy='Always'}, Container{name='mysql', command=null, environment=[Environment{key='PORT', value='3306'}, Environment{key='ROOT_PASSWORD', value='123456'}], port=8306, imagePullPolicy='Always'}], cpu=null, memory=4.0, autoCreated=false, apiVersion='v1'}
//    private static <T> void setListValue(T obj,Field field, Object value,String yamlName,Map<String, Object> yamlData) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
//        if(field.getName()==null||field.getName().length()==0)
//            field.set(obj, value);
//        List<Object> objectList = new ArrayList<>();
//        Type type= field.getGenericType();
//        Type acc=null;
//        if(type instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) type;
//            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//            for(Type t:actualTypeArguments) {
//                acc = t;
//                System.out.println(t.getTypeName() + "aaaaa");
//            }
//        }else
//            System.out.println(type.getTypeName()+"sassaa");
//        System.out.println(acc+"cccccc");
//        Class<T> claz = (Class<T>) Class.forName(acc.getTypeName());
//
//        for (Object item: ((List) value)) {
//
//            System.out.println("itemclass\t"+item.getClass());
//            if(item.getClass().isAssignableFrom(List.class)) {
//
//                setListValue(obj, field, value, yamlName, yamlData);
//            }
//            else if(item.getClass().isPrimitive()||item.getClass().equals(String.class)||item.getClass().equals(BigDecimal.class)) {
//
//                setBaseValue(obj, field, value);
//            }
//            else if(item instanceof Map) {
//                System.out.println("hashmap "+item.getClass()+field.getName()+" fildtype\t"+field.getGenericType());
//                System.out.println(((List<?>) value).get(0).getClass());
//
//                String itemClassName = field.getGenericType().getTypeName();
//                Class<?> clazz = Class.forName(itemClassName);
//                Object obj1 = clazz.getDeclaredConstructor().newInstance();
//
//                Object listItem = setValue(clazz, (Map<String, Object>) item);
//                objectList.add(listItem);
////                System.out.println(type.getTypeName()+"\tttttttt");
////                Type acc=null;
////                if(type instanceof ParameterizedType) {
////                    ParameterizedType parameterizedType = (ParameterizedType) type;
////                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
////                    for(Type t:actualTypeArguments) {
////                        acc = t;
////                        System.out.println(t.getTypeName() + "aaaaa");
////                    }
////                }else
////                    System.out.println(type.getTypeName()+"sassaa");
//                System.out.println(acc+"cccccc");
//                Class<T> clazzz = (Class<T>) Class.forName(acc.getTypeName());
//                System.out.println(clazzz);
//
//
//                field.set(obj,setValue(claz,(Map<String, Object>) yamlData.get(yamlName)));
//            }
//        }
//        System.out.println("value\t"+String.valueOf(value));
////        field.set(obj, value);
//        System.out.println("liebiao \t" + obj.toString());
//    }

    private static <T> void setBaseValue(T obj,Field field, Object value) throws IllegalAccessException {
        field.set(obj, convertValue(field.getType(), value));
        System.out.println("jichu \t" + obj.toString());
    }

    private static <T> void setMapValue(T obj,Field field, String yamlName,Map<String, Object> yamlData) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        System.out.println("maptype\t"+field.getType());
        field.set(obj, setValue(field.getType().getName(),(Map<String, Object>) yamlData.get(yamlName)));
        System.out.println("qiantao\t" + obj.toString());
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