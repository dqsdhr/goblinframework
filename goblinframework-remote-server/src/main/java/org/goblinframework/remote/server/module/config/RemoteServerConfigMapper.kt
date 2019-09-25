package org.goblinframework.remote.server.module.config

import java.io.Serializable

class RemoteServerConfigMapper : Serializable {

  var host: String? = null
  var port: Int? = null

  companion object {
    private const val serialVersionUID = -8785013343998191906L
  }
}
