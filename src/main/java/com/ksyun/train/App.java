package com.ksyun.train;

import com.ksyun.train.util.ParamParseUtil;
import com.ksyun.train.util.Pod;
import com.ksyun.train.util.test;
import com.ksyun.train.util.test2;
import com.ksyun.train.util.testyaml;
import org.yaml.snakeyaml.Yaml;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
//        System.out.println( "Hello World!" );
//        String yamlpath="src/main/java/com/ksyun/train/util/pod.yaml";
//        System.out.println(Pod.class);
        Pod pod = ParamParseUtil.parse(Pod.class, "src/main/java/com/ksyun/train/util/pod.yaml");
        System.out.println(pod.toString());
    }
}
