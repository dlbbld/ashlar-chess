package com.dlb.chess.common;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Type-system convenience wrappers around stdlib / Guava / log4j calls whose JDT-typed return is {@code @Nullable}
 * but whose runtime contract guarantees a non-null result (or whose nullness is irrelevant under the project's
 * coding conventions). Each method delegates to the underlying call and suppresses the resulting JDT nullness warning
 * via {@code @SuppressWarnings("null")} so callers receive a {@code @NonNull} value usable without further casts.
 *
 * <p>
 * No runtime null check is performed. A previous version of this class routed every call through a private
 * {@code checkResult} helper that threw {@link com.dlb.chess.common.exceptions.ProgrammingMistakeException} on a null
 * result; that check was removed because it added a branch on every {@code Nulls.*} call site, which adds up across
 * hot paths (notably the helpmate search's per-node {@code Nulls.get(Square.REAL, ordinal)} calls inside the bitboard
 * legal-move generator and the slider attack loops). The trade-off: misuse — e.g. {@link #get(Map, Object)} on a key
 * the map does not contain — now manifests as a deeper {@link NullPointerException} rather than at the {@code Nulls}
 * call site. Callers must already know the underlying call returns non-null at the call site (the documented
 * contract of every method here).
 */
public class Nulls {

  private Nulls() {
  }

  @SuppressWarnings("null")
  public static String getName(File file) {
    return file.getName();
  }

  @SuppressWarnings("null")
  public static String getAbsolutePath(File file) {
    return file.getAbsolutePath();
  }

  @SuppressWarnings("null")
  public static String nextLine(Scanner myReader) {
    return myReader.nextLine();
  }

  // Queue<E> in the JDK is not annotated with @NonNull/@Nullable, so JDT can't statically prove the head element
  // is non-null even when E is. The runtime contract guarantees a non-null result on a non-empty queue (Queue.remove
  // throws NoSuchElementException when empty).
  @SuppressWarnings("null")
  public static <E> E remove(Queue<E> queue) {
    return queue.remove();
  }

  @SuppressWarnings("null")
  public static <E> E remove(List<E> list, int index) {
    return list.remove(index);
  }

  @SuppressWarnings("null")
  public static String toString(StringBuilder stringBuilder) {
    return stringBuilder.toString();
  }

  @SuppressWarnings("null")
  public static String toString(char c) {
    return Character.toString(c);
  }

  @SuppressWarnings("null")
  public static String toString(Object obj) {
    return obj.toString();
  }

  @SuppressWarnings("null")
  public static String substring(String string, int beginIndex) {
    return string.substring(beginIndex);
  }

  @SuppressWarnings("null")
  public static String substring(String string, int beginIndex, int endIndex) {
    return string.substring(beginIndex, endIndex);
  }

  @SuppressWarnings("null")
  public static String substring(StringBuilder stringBuffer, int beginIndex, int endIndex) {
    return stringBuffer.substring(beginIndex, endIndex);
  }

  @SuppressWarnings("null")
  public static String replace(String string, String oldString, String newString) {
    return string.replace(oldString, newString);
  }

  @SuppressWarnings("null")
  public static String replace(String string, char oldChar, char newChar) {
    return string.replace(oldChar, newChar);
  }

  @SuppressWarnings("null")
  public static String toLowerCase(String string) {
    return string.toLowerCase();
  }

  @SuppressWarnings("null")
  public static String toUpperCase(String string) {
    return string.toUpperCase();
  }

  @SuppressWarnings("null")
  public static String valueOf(char charValue) {
    return String.valueOf(charValue);
  }

  @SuppressWarnings("null")
  public static String valueOf(int integerValue) {
    return String.valueOf(integerValue);
  }

  @SuppressWarnings("null")
  public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
    return String.join(delimiter, elements);
  }

  @SuppressWarnings("null")
  public static String normalizeSpace(String str) {
    return StringUtils.normalizeSpace(str);
  }

  @SuppressWarnings("null")
  public static String trim(String str) {
    return str.trim();
  }

  @SuppressWarnings("null")
  public static String capitalize(final String str) {
    return StringUtils.capitalize(str);
  }

  @SuppressWarnings("null")
  public static Pattern compile(String regex) {
    return Pattern.compile(regex);
  }

  @SuppressWarnings("null")
  @NonNull
  public static String[] split(String str, String regex) {
    return str.split(regex, -1);
  }

  @SuppressWarnings("null")
  public static Logger getLogger(Class<?> theClass) {
    return LogManager.getLogger(theClass);
  }

  @SuppressWarnings("null")
  public static <E extends Enum<E>> String name(E enumTyp) {
    return enumTyp.name();
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E, F> F get(Map<E, F> map, E key) {
    return map.get(key);
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E> E get(List<E> list, int index) {
    return list.get(index);
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E> E get(E[] list, int index) {
    return list[index];
  }

  @SuppressWarnings("null")
  public static Path pathOf(final String filePath) {
    return Path.of(filePath);
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E> E getFirst(List<E> list) {
    return list.get(0);
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E> E getLast(List<E> list) {
    return list.get(list.size() - 1);
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E> E getLast(E[] list) {
    return list[list.length - 1];
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E, F> E getKey(Entry<E, F> entry) {
    return entry.getKey();
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E, F> F getValue(Entry<E, F> entry) {
    return entry.getValue();
  }

  @SuppressWarnings("null")
  public static <E, F> Set<Map.Entry<E, F>> entrySet(Map<E, F> map) {
    return map.entrySet();
  }

  @SuppressWarnings("null")
  public static <E, F> Set<E> keySet(Map<E, F> map) {
    return map.keySet();
  }

  @SuppressWarnings("null")
  public static <E> Set<E> emptySet() {
    return Collections.emptySet();
  }

  @SuppressWarnings("null")
  @NonNull
  public static <E, F> F getOrDefault(Map<E, F> map, E key, F defaultValue) {
    return map.getOrDefault(key, defaultValue);
  }

  @SuppressWarnings("null")
  public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
    return Sets.newEnumSet(iterable, elementType);
  }

  @SuppressWarnings("null")
  public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
    return Maps.newEnumMap(type);
  }

  @SuppressWarnings("null")
  public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
    return Maps.immutableEnumMap(map);
  }

  @SuppressWarnings("null")
  public static <E> ImmutableList<E> copyOfList(Iterable<? extends E> elements) {
    return ImmutableList.copyOf(elements);
  }

  @SuppressWarnings("null")
  public static <E> ImmutableSet<E> copyOfSet(Collection<? extends E> elements) {
    return ImmutableSet.copyOf(elements);
  }

  @SuppressWarnings("null")
  public static <K, V> ImmutableMap<K, V> copyOfMap(Map<? extends K, ? extends V> map) {
    return ImmutableMap.copyOf(map);
  }

  @SuppressWarnings({ "unchecked", "null" })
  public static <T> List<T> asList(T... a) {
    return Arrays.asList(a);
  }

  @SuppressWarnings("null")
  public static Path pathResolve(final Path directoryPath, final String filePath) {
    return directoryPath.resolve(filePath);
  }

  @SuppressWarnings("null")
  public static Path pathRelativize(final Path directoryPath, final Path other) {
    return directoryPath.relativize(other);
  }

  @SuppressWarnings("null")
  public static Path getFileName(Path path) {
    return path.getFileName();
  }

  @SuppressWarnings("null")
  public static Path getParent(Path path) {
    return path.getParent();
  }

  @SuppressWarnings("null")
  public static Path toAbsolutePath(Path path) {
    return path.toAbsolutePath();
  }

  @SuppressWarnings("null")
  public static String format(String format, Object... args) {
    return String.format(format, args);
  }

  @SuppressWarnings("null")
  public static <E> List<E> subList(List<E> list, int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

  /**
   * Wraps a {@code main(String[] args)} array as a properly-annotated {@code @NonNull List<@NonNull String>}.
   * Bypasses the varargs nullness-inference trap that fires when {@code Nulls.listOf(args)} is used: with
   * {@code @NonNullByDefault} active, JDT cannot prove that the {@code String} elements of {@code args} are
   * non-null, so the implicit array conversion warns.
   */
  @SuppressWarnings("null")
  public static List<String> argsAsList(String[] args) {
    return Arrays.asList(args);
  }

  @SuppressWarnings({ "unchecked", "null" })
  public static <E> Set<E> setOf(E... items) {
    return Set.of(items);
  }

  @SuppressWarnings({ "unchecked", "null" })
  public static <E> List<E> listOf(E... items) {
    return List.of(items);
  }

  @SuppressWarnings("null")
  public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> enumClass) {
    return EnumSet.noneOf(enumClass);
  }

}
