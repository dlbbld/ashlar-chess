package com.dlb.chess.common;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.eclipse.jdt.annotation.Nullable;

import com.dlb.chess.common.exceptions.ProgrammingMistakeException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Nulls {

  private Nulls() {
  }

  @NonNull
  private static <E> E checkResult(@Nullable E result) {
    if (result == null) {
      throw new ProgrammingMistakeException("Assumed value is not null");
    }
    return result;
  }

  @SuppressWarnings("null")
  public static String getName(File file) {
    return file.getName();
  }

  public static String getAbsolutePath(File file) {
    return checkResult(file.getAbsolutePath());
  }

  public static String nextLine(Scanner myReader) {
    return checkResult(myReader.nextLine());
  }

  // Queue<E> in the JDK is not annotated with @NonNull/@Nullable, so JDT can't statically prove the head element
  // is non-null even when E is. The runtime contract guarantees a non-null result on a non-empty queue (Queue.remove
  // throws NoSuchElementException when empty); checkResult enforces that contract for the type system.
  public static <E> E remove(Queue<E> queue) {
    return checkResult(queue.remove());
  }

  public static <E> E remove(List<E> list, int index) {
    return checkResult(list.remove(index));
  }

  public static String toString(StringBuilder stringBuilder) {
    return checkResult(stringBuilder.toString());
  }

  public static String toString(char c) {
    return checkResult(Character.toString(c));
  }

  public static String toString(Object obj) {
    return checkResult(obj.toString());
  }

  public static String substring(String string, int beginIndex) {
    return checkResult(string.substring(beginIndex));
  }

  public static String substring(String string, int beginIndex, int endIndex) {
    return checkResult(string.substring(beginIndex, endIndex));
  }

  public static String substring(StringBuilder stringBuffer, int beginIndex, int endIndex) {
    return checkResult(stringBuffer.substring(beginIndex, endIndex));
  }

  public static String replace(String string, String oldString, String newString) {
    return checkResult(string.replace(oldString, newString));
  }

  public static String replace(String string, char oldChar, char newChar) {
    return checkResult(string.replace(oldChar, newChar));
  }

  public static String toLowerCase(String string) {
    return checkResult(string.toLowerCase());
  }

  public static String toUpperCase(String string) {
    return checkResult(string.toUpperCase());
  }

  public static String valueOf(char charValue) {
    return checkResult(String.valueOf(charValue));
  }

  public static String valueOf(int integerValue) {
    return checkResult(String.valueOf(integerValue));
  }

  public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
    return checkResult(String.join(delimiter, elements));
  }

  public static String normalizeSpace(String str) {
    return checkResult(StringUtils.normalizeSpace(str));
  }

  public static String trim(String str) {
    return checkResult(str.trim());
  }

  public static String capitalize(final String str) {
    return checkResult(StringUtils.capitalize(str));
  }

  public static Pattern compile(String regex) {
    return checkResult(Pattern.compile(regex));
  }

  @SuppressWarnings("null")
  @NonNull
  public static String[] split(String str, String regex) {
    return checkResult(str.split(regex, -1));
  }

  public static Logger getLogger(Class<?> theClass) {
    return checkResult(LogManager.getLogger(theClass));
  }

  public static <E extends Enum<E>> String name(E enumTyp) {
    return checkResult(enumTyp.name());
  }

  @NonNull
  public static <E, F> F get(Map<E, F> map, E key) {
    return checkResult(map.get(key));
  }

  @NonNull
  public static <E> E get(List<E> list, int index) {
    return checkResult(list.get(index));
  }

  @NonNull
  public static <E> E get(E[] list, int index) {
    return checkResult(list[index]);
  }

  public static Path pathsGet(final String first, String... more) {
    return checkResult(Paths.get(first, more)); // not null by API
  }

  public static Path pathOf(final String filePath) {
    return checkResult(Path.of(filePath)); // not null by API
  }

  @NonNull
  public static <E> E getFirst(List<E> list) {
    return checkResult(list.get(0));
  }

  @NonNull
  public static <E> E getLast(List<E> list) {
    return checkResult(list.get(list.size() - 1));
  }

  @NonNull
  public static <E> E getLast(E[] list) {
    return checkResult(list[list.length - 1]);
  }

  @NonNull
  public static <E, F> E getKey(Entry<E, F> entry) {
    return checkResult(entry.getKey());
  }

  @NonNull
  public static <E, F> F getValue(Entry<E, F> entry) {
    return checkResult(entry.getValue());
  }

  @SuppressWarnings("null")
  public static <E, F> Set<Map.Entry<E, F>> entrySet(Map<E, F> map) {
    return checkResult(map.entrySet());
  }

  public static <E, F> Set<E> keySet(Map<E, F> map) {
    return checkResult(map.keySet());
  }

  public static <E> Set<E> emptySet() {
    return checkResult(Collections.emptySet());
  }

  @NonNull
  public static <E, F> F getOrDefault(Map<E, F> map, E key, F defaultValue) {
    return checkResult(map.getOrDefault(key, defaultValue));
  }

  public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
    return checkResult(Sets.newEnumSet(iterable, elementType));
  }

  public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
    return checkResult(Maps.newEnumMap(type));
  }

  public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
    return checkResult(Maps.immutableEnumMap(map));
  }

  public static <E> ImmutableList<E> copyOfList(Iterable<? extends E> elements) {
    return checkResult(ImmutableList.copyOf(elements));
  }

  public static <E> ImmutableSet<E> copyOfSet(Collection<? extends E> elements) {
    return checkResult(ImmutableSet.copyOf(elements));
  }

  public static <K, V> ImmutableMap<K, V> copyOfMap(Map<? extends K, ? extends V> map) {
    return checkResult(ImmutableMap.copyOf(map));
  }

  @SuppressWarnings({ "unchecked" })
  public static <T> List<T> asList(T... a) {
    return checkResult(Arrays.asList(a));
  }

  public static Path pathResolve(final Path directoryPath, final String filePath) {
    return checkResult(directoryPath.resolve(filePath));
  }

  public static Path pathRelativize(final Path directoryPath, final Path other) {
    return checkResult(directoryPath.relativize(other));
  }

  public static Path getFileName(Path path) {
    return checkResult(path.getFileName());
  }

  public static Path getParent(Path path) {
    return checkResult(path.getParent());
  }

  public static Path toAbsolutePath(Path path) {
    return checkResult(path.toAbsolutePath());
  }

  public static String format(String format, Object... args) {
    return checkResult(String.format(format, args));
  }

  public static <E> List<E> subList(List<E> list, int fromIndex, int toIndex) {
    return checkResult(list.subList(fromIndex, toIndex));
  }

  /**
   * Wraps a {@code main(String[] args)} array as a properly-annotated {@code @NonNull List<@NonNull String>}, runtime-
   * checking each element. Bypasses the varargs nullness-inference trap that fires when {@code Nulls.listOf(args)} is
   * used: with @NonNullByDefault active, JDT cannot prove that the {@code String} elements of {@code args} are
   * non-null, so the implicit array conversion warns.
   */
  public static List<String> argsAsList(String[] args) {
    final List<String> result = new ArrayList<>(args.length);
    for (final @Nullable String arg : args) {
      result.add(checkResult(arg));
    }
    return result;
  }

  @SuppressWarnings({ "unchecked" })
  public static <E> Set<E> setOf(E... items) {
    return checkResult(Set.of(items));
  }

  @SafeVarargs
  public static <E> ImmutableList<E> listOf(E... items) {
    return checkResult(ImmutableList.copyOf(items));
  }

  public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> enumClass) {
    return checkResult(EnumSet.noneOf(enumClass));
  }

}
