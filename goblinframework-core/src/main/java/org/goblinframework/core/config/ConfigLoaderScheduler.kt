package org.goblinframework.core.config

import org.goblinframework.core.event.EventBus
import org.goblinframework.core.event.GoblinEventContext
import org.goblinframework.core.event.dsl.MinuteTimerEventListener

class ConfigLoaderScheduler internal constructor(private val configLoader: ConfigLoader)
  : MinuteTimerEventListener() {

  override fun onEvent(context: GoblinEventContext) {
    if (configLoader.reload()) {
      EventBus.publish(ConfigModifiedEvent())
    }
  }
}