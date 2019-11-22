package com.sap.mervyn.thread.ch7.case01;

import com.sap.mervyn.thread.util.Tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ConfigurationManager {
    INSTANCE;

    protected final Set<ConfigEventListener> listeners = new HashSet<>();

    public Configuration load(String name) {
        Configuration cfg = loadConfigurationFromDB(name);
        synchronized (this) {
            for (ConfigEventListener listener : listeners) {
                listener.onConfigLoaded(cfg);
            }
        }

        return cfg;
    }

    private Configuration loadConfigurationFromDB(String name) {
        // 模拟从数据库加载配置数据
        Tools.randomPause(50);
        Configuration cfg = new Configuration(name, 0);
        cfg.setProperty("url", "https://github.com/chmervyn");
        cfg.setProperty("connectionTimeout", "2000");
        cfg.setProperty("readTimeout", "2000");

        return cfg;
    }

    public synchronized void registerListener(ConfigEventListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ConfigEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void update(String name, int newVersion, Map<String, String> properties) {
        for (ConfigEventListener listener : listeners) {
            // 这个外部方法调用可能导致死锁！
            listener.onConfigUpdated(name, newVersion, properties);
        }
    }


}
