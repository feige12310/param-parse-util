package com.ksyun.train.util;

import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;
import java.util.List;
import com.ksyun.train.util.ParamParseUtil;
import com.ksyun.train.util.Pod;
import org.yaml.snakeyaml.Yaml;
import java.util.List;

import java.io.IOException;
public class testyaml {
    private int cpu;
    private String Container="sasas";

    private BigDecimal memory;
    @SkipMappingValueAnnotation
    private String apiVersion = "v1";

    public String getApiVersion() {
        return apiVersion;
    }

    public BigDecimal getMemory() {
        return memory;
    }

    public int getCpu() {
        return cpu;
    }
    private List<String> command;
    private Metadata metadata;
    public static class Metadata {
        private long generation;

        private String name;

        public long getGeneration() {
            return generation;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Metadata{" +
                    "generation=" + generation +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "testyaml{" +
                "cpu=" + cpu +
                ", Container='" + Container + '\'' +
                ", memory=" + memory +
                ", apiVersion='" + apiVersion + '\'' +
                ", command=" + command +
                ", metadata=" + metadata +
                '}';
    }
}