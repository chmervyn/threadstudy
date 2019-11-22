package com.sap.mervyn.thread.ch7.case01;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final String name;
    private volatile int version;

    private volatile Map<String, String> configItemMap;

    public Configuration(String name, int version) {
        this.name = name;
        this.version = version;
        configItemMap = new HashMap<>();
    }

    public String getName() { return name; }

    public void setProperty(String key, String value) { configItemMap.put(key, value); }

    public String getProperty(String key) { return configItemMap.get(key); }

    public void update(Map<String, String> properties) { configItemMap = properties; }

    public int getVersion() { return version; }

    public void setVersion(int version) { this.version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;

        if (name == null) {
            if (that.name != null) {
                return false;
            }
        } else if (!name.equals(that.name)) {
            return false;
        }

        if (version != that.version) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + version;

        return result;
    }
}
