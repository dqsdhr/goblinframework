package org.goblinframework.remote.client.invocation.filter

import org.goblinframework.api.annotation.Singleton
import org.goblinframework.remote.client.invocation.RemoteClientInvocation
import org.goblinframework.remote.core.filter.RemoteFilterChain

@Singleton
class SelectRouterFilter private constructor() : AbstractInternalFilter() {

  companion object {
    @JvmField val INSTANCE = SelectRouterFilter()
  }

  override fun doFilter(invocation: RemoteClientInvocation, chain: RemoteFilterChain<RemoteClientInvocation>) {
    chain.filter(invocation)
  }
}