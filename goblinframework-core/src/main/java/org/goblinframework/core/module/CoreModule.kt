package org.goblinframework.core.module

import org.goblinframework.api.annotation.Install
import org.goblinframework.api.system.GoblinModule
import org.goblinframework.api.system.IModule
import org.goblinframework.api.system.ModuleFinalizeContext
import org.goblinframework.api.system.ModuleInstallContext
import org.goblinframework.core.compression.CompressorManager
import org.goblinframework.core.module.management.CoreManagement
import org.goblinframework.core.serialization.SerializerManager

@Install
class CoreModule : IModule {

  override fun id(): GoblinModule {
    return GoblinModule.CORE
  }

  override fun managementEntrance(): String? {
    return "/goblin/core/index.do"
  }

  override fun install(ctx: ModuleInstallContext) {
    ctx.registerManagementController(CoreManagement.INSTANCE)
  }

  override fun finalize(ctx: ModuleFinalizeContext) {
    CompressorManager.INSTANCE.dispose()
    SerializerManager.INSTANCE.dispose()
  }
}