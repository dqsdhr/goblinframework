package org.goblinframework.transport.server.module

import org.goblinframework.api.common.Install
import org.goblinframework.api.system.GoblinSubModule
import org.goblinframework.api.system.ISubModule
import org.goblinframework.api.system.ModuleFinalizeContext
import org.goblinframework.transport.server.channel.TransportServerManager

@Install
class TransportServerModule : ISubModule {

  override fun id(): GoblinSubModule {
    return GoblinSubModule.TRANSPORT_SERVER
  }

  override fun finalize(ctx: ModuleFinalizeContext) {
    TransportServerManager.INSTANCE.dispose()
  }
}