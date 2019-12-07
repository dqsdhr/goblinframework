package org.goblinframework.dao.mysql.module

import org.goblinframework.api.annotation.Install
import org.goblinframework.core.system.*
import org.goblinframework.database.core.module.DatabaseModule
import org.goblinframework.database.mysql.client.MysqlClientManager
import org.goblinframework.database.mysql.mapping.MysqlEntityMappingBuilderProvider
import org.goblinframework.database.mysql.module.config.MysqlConfigManager
import org.goblinframework.database.mysql.module.test.RebuildMysqlTableBeforeTestMethod

@Install
class MysqlDaoModule : ISubModule {

  override fun id(): GoblinSubModule {
    return GoblinSubModule.DAO_MYSQL
  }

  override fun install(ctx: ModuleInstallContext) {
    val parent = ctx.getExtension(DatabaseModule::class.java)
    parent?.registerEntityMappingBuilderProvider(MysqlEntityMappingBuilderProvider.INSTANCE)
    ctx.registerTestExecutionListener(RebuildMysqlTableBeforeTestMethod.INSTANCE)
  }

  override fun initialize(ctx: ModuleInitializeContext) {
    MysqlConfigManager.INSTANCE.initialize()
  }

  override fun finalize(ctx: ModuleFinalizeContext) {
    MysqlClientManager.INSTANCE.dispose()
    MysqlConfigManager.INSTANCE.dispose()
  }
}