package org.goblinframework.core.bootstrap

import org.goblinframework.core.event.EventBus
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class GoblinModuleManager private constructor() {

  companion object {
    @JvmField val INSTANCE = GoblinModuleManager()
  }

  init {
    EventBus.subscribe(GoblinChildModuleEventListener.INSTANCE)
  }

  private val initialize = AtomicBoolean()
  private val bootstrap = AtomicBoolean()
  private val shutdown = AtomicBoolean()
  private val finalize = AtomicBoolean()

  fun executeInitialize(): GoblinModuleManager {
    if (!initialize.compareAndSet(false, true)) {
      return this
    }
    val ctx = GoblinModuleInitializeContext.INSTANCE
    for (name in GoblinModuleDefinition.moduleNames) {
      val module = GoblinModuleLoader.INSTANCE.getGoblinModule(name) ?: continue
      module.initialize(ctx)
      GoblinBootstrap.LOGGER.info("Initialize {${module.name()}}")
    }
    for (module in GoblinExtensionModuleLoader.INSTANCE.getGoblinExtensionModules()) {
      module.initialize(ctx)
      GoblinBootstrap.LOGGER.info("Initialize (${module.name()})")
    }
    return this
  }

  fun executeBootstrap() {
    if (!bootstrap.compareAndSet(false, true)) {
      return
    }
    val ctx = GoblinModuleBootstrapContext()
    for (name in GoblinModuleDefinition.moduleNames) {
      val module = GoblinModuleLoader.INSTANCE.getGoblinModule(name) ?: continue
      module.bootstrap(ctx)
      GoblinBootstrap.LOGGER.info("Bootstrap {${module.name()}}")
    }
    for (module in GoblinExtensionModuleLoader.INSTANCE.getGoblinExtensionModules()) {
      module.bootstrap(ctx)
      GoblinBootstrap.LOGGER.info("Bootstrap (${module.name()})")
    }
  }

  fun executeFinalize() {
    if (!finalize.compareAndSet(false, true)) {
      return
    }
    val future = EventBus.execute {
      val ctx = GoblinModuleFinalizeContext()
      for (name in GoblinModuleDefinition.moduleNames.reversed()) {
        val module = GoblinModuleLoader.INSTANCE.getGoblinModule(name) ?: continue
        module.finalize(ctx)
        GoblinBootstrap.LOGGER.info("Finalize {${module.name()}}")
      }
      for (module in GoblinExtensionModuleLoader.INSTANCE.getGoblinExtensionModules().reversed()) {
        module.finalize(ctx)
        GoblinBootstrap.LOGGER.info("Finalize (${module.name()})")
      }
    }
    try {
      future.awaitUninterruptibly(1, TimeUnit.MINUTES)
    } catch (ignore: Throwable) {
    }
  }

}