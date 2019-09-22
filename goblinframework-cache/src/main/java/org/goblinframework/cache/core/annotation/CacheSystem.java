package org.goblinframework.cache.core.annotation;

import org.goblinframework.cache.core.cache.GoblinCache;
import org.goblinframework.cache.core.cache.GoblinCacheBuilder;
import org.goblinframework.cache.core.cache.GoblinCacheBuilderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CacheSystem {

  NOP,
  JVM,
  CBS,
  RDS;

  @Nullable
  public GoblinCacheBuilder getCacheBuilder() {
    GoblinCacheBuilderManager manager = GoblinCacheBuilderManager.INSTANCE;
    return manager.getCacheBuilder(this);
  }

  @Nullable
  public GoblinCache getCache(@NotNull String name) {
    GoblinCacheBuilder cacheBuilder = getCacheBuilder();
    return cacheBuilder == null ? null : cacheBuilder.getCache(name);
  }

}
