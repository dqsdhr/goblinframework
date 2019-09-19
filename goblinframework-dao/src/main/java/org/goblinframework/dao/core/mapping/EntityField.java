package org.goblinframework.dao.core.mapping;

import org.apache.commons.lang3.mutable.MutableObject;
import org.goblinframework.core.reflection.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Set;

abstract public class EntityField {

  private final EntityFieldNameResolver nameResolver;
  private final Field field;
  private final MutableObject<String> resolvedName = new MutableObject<>(null);

  public EntityField(@NotNull EntityFieldNameResolver nameResolver,
                     @NotNull Field field) {
    this.nameResolver = nameResolver;
    this.field = field;
  }

  @Nullable
  public <T extends Annotation> T getAnnotation(@NotNull Class<T> annotationClass) {
    return field.getField().getAnnotation(annotationClass);
  }

  @NotNull
  public synchronized String getName() {
    String resolved = resolvedName.getValue();
    if (resolved != null) return resolved;
    resolved = nameResolver.resolve(this);
    resolvedName.setValue(resolved);
    return resolved;
  }

  @NotNull
  public Field getField() {
    return field;
  }

  public Object getValue(@NotNull Object obj) {
    return field.get(obj);
  }

  public void setValue(@NotNull Object obj, Object value) {
    field.set(obj, value);
  }

  @Nullable
  abstract public Set<Class<?>> allowedFieldTypes();
}
