package org.goblinframework.database.mysql.persistence;

import org.goblinframework.api.database.Id;
import org.goblinframework.api.database.Table;

/**
 * 只有主键的实体
 */
@Table(table = "UT_UNIQUE_ID_T")
public class UniqueIdEntity {

  @Id(Id.Generator.AUTO_INC)
  public Long id;
}