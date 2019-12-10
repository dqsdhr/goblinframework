package org.goblinframework.example.embedded

import org.goblinframework.bootstrap.core.StandaloneServer
import org.goblinframework.core.container.GoblinSpringContainer
import org.goblinframework.core.container.SpringContainer
import org.goblinframework.embedded.core.handler.DispatchServletHandler
import org.goblinframework.embedded.core.resource.ClassPathStaticResourceManager
import org.goblinframework.embedded.core.resource.MapStaticResourceBuffer
import org.goblinframework.embedded.server.EmbeddedServerManager
import org.goblinframework.embedded.server.EmbeddedServerMode
import org.goblinframework.embedded.setting.ServerSetting
import org.goblinframework.webmvc.handler.RequestHandlerManagerBuilder
import org.goblinframework.webmvc.setting.RequestHandlerSetting

@GoblinSpringContainer("/config/goblin-example-embedded.xml")
class Server : StandaloneServer() {

  override fun doService(container: SpringContainer?) {
    val ctx = container!!.applicationContext
    val handler = DispatchServletHandler()
    handler.registerWelcomeFile("/index.goblin")

    val setting = RequestHandlerSetting.builder()
        .name("GOBLIN")
        .applyControllerSetting { it.applicationContext(ctx) }
        .applyInterceptorSetting { it.applicationContext(ctx) }
        .applyViewResolverSetting { it.applicationContext(ctx) }
        .build()
    val handlerManager = RequestHandlerManagerBuilder.INSTANCE.createRequestHandlerManager(setting)
    handler.registerRequestHandlerManager(handlerManager, ".goblin")

    val resourceBuffer = MapStaticResourceBuffer(4096)
    val resourceManager = ClassPathStaticResourceManager(resourceBuffer, "/static", "/WEB-INF/static")
    handler.registerStaticResourceManager(resourceManager)

    val serverSetting = ServerSetting.builder()
        .name("GOBLIN")
        .mode(EmbeddedServerMode.JETTY)
        .applyNetworkSetting {
          it.port(9797)
        }
        .applyHandlerSetting {
          it.contextPath("/")
          it.servletHandler(handler)
        }
        .build()
    EmbeddedServerManager.INSTANCE.createServer(serverSetting).start()
  }

}

fun main(args: Array<String>) {
  Server().bootstrap(args)
}