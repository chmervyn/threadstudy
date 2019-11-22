package com.sap.mervyn.thread.ch7.case01;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum ConfigurationHelper implements ConfigEventListener {
    INSTANCE;

    //final ConfigurationManager configManager;
    final ConfigurationManagerV2 configManager;
    final ConcurrentMap<String, Configuration> cachedConfig;

    private ConfigurationHelper() {
        //configManager = ConfigurationManager.INSTANCE;
        configManager = ConfigurationManagerV2.INSTANCE;
        cachedConfig = new ConcurrentHashMap<>();
    }

    public Configuration getConfig(String name) {
        Configuration cfg;

        cfg = getCachedConfig(name);

        if (cfg == null) {
            synchronized (this) {
                cfg = getCachedConfig(name);
                if (cfg == null) {
                    cfg = configManager.load(name);
                    cachedConfig.put(name, cfg);
                }
            }
        }

        return cfg;
    }

    public Configuration getCachedConfig(String name) {
        return cachedConfig.get(name);
    }

    public ConfigurationHelper init() {
        configManager.registerListener(this);
        return this;
    }

    @Override
    public void onConfigLoaded(Configuration cfg) {
        cachedConfig.putIfAbsent(cfg.getName(), cfg);
    }

    @Override
    public void onConfigUpdated(String name, int newVersion, Map<String, String> properties) {
        Configuration cachedConfig = getCachedConfig(name);

        // 更新内容和版本这两个操作必须是原子操作
        synchronized (this) {
            if (cachedConfig != null) {
                cachedConfig.update(properties);
                cachedConfig.setVersion(newVersion);
            }
        }
    }
}
