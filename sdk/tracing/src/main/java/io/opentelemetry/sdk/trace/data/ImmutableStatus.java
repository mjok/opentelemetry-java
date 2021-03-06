/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.trace.data;

import com.google.auto.value.AutoValue;
import io.opentelemetry.sdk.trace.data.SpanData.Status;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.StatusCanonicalCode;
import java.util.EnumMap;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Defines the status of a {@link Span} by providing a standard {@link StatusCanonicalCode} in
 * conjunction with an optional descriptive message. Instances of {@code Status} are created by
 * starting with the template for the appropriate {@link StatusCanonicalCode} and supplementing it
 * with additional information: {@code Status.NOT_FOUND.withDescription("Could not find
 * 'important_file.txt'");}
 */
@AutoValue
@Immutable
public abstract class ImmutableStatus implements SpanData.Status {
  /**
   * The operation has been validated by an Application developers or Operator to have completed
   * successfully.
   */
  public static final SpanData.Status OK = createInternal(StatusCanonicalCode.OK, null);

  /** The default status. */
  public static final SpanData.Status UNSET = createInternal(StatusCanonicalCode.UNSET, null);

  /** The operation contains an error. */
  public static final SpanData.Status ERROR = createInternal(StatusCanonicalCode.ERROR, null);

  // Visible for test
  static final EnumMap<StatusCanonicalCode, Status> codeToStatus =
      new EnumMap<>(StatusCanonicalCode.class);

  static {
    codeToStatus.put(StatusCanonicalCode.UNSET, UNSET);
    codeToStatus.put(StatusCanonicalCode.OK, OK);
    codeToStatus.put(StatusCanonicalCode.ERROR, ERROR);

    // Ensure all values are in the map, even if we don't have constants defined.
    // This can happen if the API version is newer than the SDK and new values were added there.
    StatusCanonicalCode[] codes = StatusCanonicalCode.values();
    for (StatusCanonicalCode code : codes) {
      Status status = codeToStatus.get(code);
      if (status == null) {
        codeToStatus.put(code, createInternal(code, null));
      }
    }
  }

  /**
   * Creates a derived instance of {@code Status} with the given description.
   *
   * @param description the new description of the {@code Status}.
   * @return The newly created {@code Status} with the given description.
   * @since 0.1.0
   */
  public static SpanData.Status create(
      StatusCanonicalCode canonicalCode, @Nullable String description) {
    if (description == null) {
      return codeToStatus.get(canonicalCode);
    }
    return createInternal(canonicalCode, description);
  }

  private static SpanData.Status createInternal(
      StatusCanonicalCode canonicalCode, @Nullable String description) {
    return new AutoValue_ImmutableStatus(canonicalCode, description);
  }
}
