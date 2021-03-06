package com.artfulbits.ui.binding.reflection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Methods for manipulating classes via reflection. */
@SuppressWarnings("unused")
public final class ReflectionUtils {
  /* [ CONSTANTS ] ================================================================================================= */

  /** Caching of the reflected information. Class-to-fields. */
  private static final Map<Class<?>, List<Field>> sCacheFields = new HashMap<>();

  /** Caching of the reflected information. Class-to-methods. */
  private static final Map<Class<?>, List<Method>> sCacheMethods = new HashMap<>();

  /* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Find in list of fields specific one by its name.
   *
   * @param fields list of fields. Should be sorted by name!
   * @param name field name which we want to find.
   * @return found field, otherwise {@code null}.
   */
  @Nullable
  public static Field findField(@NonNull final List<Field> fields, @NonNull final String name) {
    final int index = Collections.binarySearch(fields, name, new SearchByFieldNameComparator());

    // field found
    if (index >= 0 && index < fields.size()) {
      return fields.get(index);
    }

    return null;
  }

  /**
   * Find in list of methods specific one by its name.
   *
   * @param methods list of methods. Should be sorted by name!
   * @param name field name which we want to find.
   * @return found field, otherwise {@code null}.
   */
  @Nullable
  public static Method findMethod(@NonNull final List<Method> methods, @NonNull final String name) {
    final int index = Collections.binarySearch(methods, name, new SearchByMethodNameComparator());

    if (index >= 0 && index < methods.size()) {
      return methods.get(index);
    }

    return null;
  }

  /**
   * Extract all inherited fields from class. Results are sorted by name.
   *
   * @param type type to check
   * @return list of found fields.
   */
  @NonNull
  public synchronized static List<Field> getAllFields(@NonNull final Class<?> type) {
    if (sCacheFields.containsKey(type)) {
      return sCacheFields.get(type);
    }

    final ArrayList<Field> results = new ArrayList<>();
    sCacheFields.put(type, results);

    Class<?> i = type;
    while (i != null && i != Object.class) {
      for (final Field field : i.getDeclaredFields()) {
        if (!field.isSynthetic()) {
          results.add(field);
        }
      }

      i = i.getSuperclass();
    }

    Collections.sort(results, new ByFieldName());

    return results;
  }

  /**
   * Compose list of all methods declared in class.
   *
   * @param type type to check
   * @return list of found methods.
   */
  @NonNull
  public synchronized static List<Method> getAllMethods(@NonNull final Class<?> type) {
    if (sCacheMethods.containsKey(type)) {
      return sCacheMethods.get(type);
    }

    final ArrayList<Method> results = new ArrayList<>();
    sCacheMethods.put(type, results);

    Class<?> i = type;
    while (i != null && i != Object.class) {
      for (final Method method : i.getDeclaredMethods()) {
        if (!method.isSynthetic()) {
          results.add(method);
        }
      }

      i = i.getSuperclass();
    }

    Collections.sort(results, new ByMethodName());

    return results;
  }

	/* [ NESTED DECLARATIONS ] ======================================================================================= */

  /** Sort fields by name. */
  private static final class ByFieldName implements java.util.Comparator<Field> {
    /** {@inheritDoc} */
    @Override
    public int compare(final Field lhs, final Field rhs) {
      return lhs.getName().compareTo(rhs.getName());
    }
  }

  /** Sort methods by name. */
  private static final class ByMethodName implements java.util.Comparator<Method> {
    /** {@inheritDoc} */
    @Override
    public int compare(final Method lhs, final Method rhs) {
      return lhs.getName().compareTo(rhs.getName());
    }
  }

  /** Search in fields collection by field name. */
  private static final class SearchByFieldNameComparator implements Comparator<Object> {
    /** {@inheritDoc} */
    @Override
    public int compare(Object lhs, Object rhs) {
      if (lhs instanceof Field && rhs instanceof String) {
        return ((Field) lhs).getName().compareTo((String) rhs);
      }

      if (lhs instanceof String && rhs instanceof Field) {
        return ((String) lhs).compareTo(((Field) rhs).getName());
      }

      throw new AssertionError("unexpected");
    }
  }

  /** Search in fields collection by field name. */
  private static final class SearchByMethodNameComparator implements Comparator<Object> {
    /** {@inheritDoc} */
    @Override
    public int compare(Object lhs, Object rhs) {
      if (lhs instanceof Method && rhs instanceof String) {
        return ((Method) lhs).getName().compareTo((String) rhs);
      }

      if (lhs instanceof String && rhs instanceof Method) {
        return ((String) lhs).compareTo(((Method) rhs).getName());
      }

      throw new AssertionError("unexpected");
    }
  }
}
