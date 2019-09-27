package org.goblinframework.core.module

import org.goblinframework.api.core.Install
import org.goblinframework.core.event.EventBusBoss
import org.goblinframework.core.event.GoblinCallbackEventListener
import org.goblinframework.core.event.TimerEventGenerator
import org.goblinframework.core.module.management.EventManagement
import org.goblinframework.core.system.*

@Install
class EventModule : IModule {

  override fun id(): GoblinModule {
    return GoblinModule.EVENT
  }

  override fun managementEntrance(): String? {
    return "/goblin/event/index.do"
  }

  override fun install(ctx: ModuleInstallContext) {
    ctx.registerEventChannel("/goblin/core", 32768, 0)
    ctx.registerEventChannel("/goblin/timer", 32768, 4)
    ctx.registerEventChannel("/goblin/monitor", 65536, 8)
    ctx.subscribeEventListener(GoblinCallbackEventListener.INSTANCE)
    ctx.subscribeEventListener(SubModuleEventListener.INSTANCE)
    TimerEventGenerator.INSTANCE.install()
    ctx.registerManagementController(EventManagement.INSTANCE)
  }

  override fun initialize(ctx: ModuleInitializeContext) {
    EventBusBoss.INSTANCE.initialize()
  }

  override fun finalize(ctx: ModuleFinalizeContext) {
    TimerEventGenerator.INSTANCE.dispose()
    EventBusBoss.INSTANCE.dispose()
  }
}