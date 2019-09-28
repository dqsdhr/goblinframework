package org.goblinframework.cache.core.provider

import org.goblinframework.api.annotation.Singleton
import org.goblinframework.api.cache.Cache
import org.goblinframework.api.cache.CacheBuilder
import org.goblinframework.api.cache.CacheSystem

@Singleton
class NoOpCacheBuilder private constructor() : CacheBuilder {

  companion object {
    @JvmField val INSTANCE = NoOpCacheBuilder()
  }

  override fun system(): CacheSystem {
    return CacheSystem.NOP
  }

  override fun cache(name: String): Cache {
    return NoOpCache.INSTANCE
  }
}