package org.burgas.entityreflection.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MachineMessages {

    MACHINE_NOT_FOUND("Machine not found"),

    MACHINE_NAME_FIELD_EMPTY("Machine name field is empty"),
    MACHINE_DESCRIPTION_FIELD_EMPTY("Machine description field is empty"),
    MACHINE_FIELD_COST_EMPTY("Machine field cost is empty");

    private final String message;
}
