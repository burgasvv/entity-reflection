package org.burgas.entityreflection.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyMessages {

    COMPANY_NOT_FOUND("Company not found"),
    COMPANY_FIELD_NAME_EMPTY("Company field name is empty"),
    COMPANY_FIELD_DESCRIPTION_EMPTY("Company field description is empty");

    private final String message;
}
