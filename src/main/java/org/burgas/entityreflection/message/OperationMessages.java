package org.burgas.entityreflection.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationMessages {

    OPERATION_NOT_FOUND("Operation not found"),

    OPERATION_TYPE_FIELD_EMPTY("Operation type field is empty"),
    OPERATION_AMOUNT_FIELD_EMPTY("Operation amount field is empty");

    private final String message;
}
