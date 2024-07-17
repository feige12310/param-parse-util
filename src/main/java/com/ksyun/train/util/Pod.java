package com.ksyun.train.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;

public class Pod {

    private Metadata metadata;

    private List<Container> container;

    private Integer cpu ;

    private BigDecimal memory;

    private boolean autoCreated = true;

    @SkipMappingValueAnnotation
    private String apiVersion = "v1";

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Container> getContainer() {
        return container;
    }

    public Integer getCpu() {
        return cpu;
    }

    public BigDecimal getMemory() {
        return memory;
    }

    public boolean isAutoCreated() {
        return autoCreated;
    }

    public String getApiVersion() {
        return apiVersion;
    }

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

    public static class Container {
        private String name;

        private List<String> command;

        private List<Environment> environment;

        private BigDecimal port;

        @SkipMappingValueAnnotation
        private String imagePullPolicy = "Always";

        public String getName() {
            return name;
        }

        public List<String> getCommand() {
            return command;
        }

        public List<Environment> getEnvironment() {
            return environment;
        }

        public BigDecimal getPort() {
            return port;
        }

        public String getImagePullPolicy() {
            return imagePullPolicy;
        }

        @Override
        public String toString() {
            return "Container{" +
                    "name='" + name + '\'' +
                    ", command=" + command +
                    ", environment=" + environment +
                    ", port=" + port +
                    ", imagePullPolicy='" + imagePullPolicy + '\'' +
                    '}';
        }
    }

    public static class Environment {
        private String key;

        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Environment{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }



    @Override
    public String toString() {
        return "Pod{" +
                "metadata=" + metadata +
                ", container=" + container +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", autoCreated=" + autoCreated +
                ", apiVersion='" + apiVersion + '\'' +
                '}';
    }
}