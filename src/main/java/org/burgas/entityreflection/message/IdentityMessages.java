package org.burgas.entityreflection.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdentityMessages {

    IDENTITY_NOT_FOUND("Identity not found"),

    IDENTITY_SECURE_OBJECT_EMPTY("Identity secure object is empty"),
    IDENTITY_AUTHORITY_FIELD_EMPTY("Identity authority field is empty"),
    IDENTITY_USERNAME_FIELD_EMPTY("Identity username field is empty"),
    IDENTITY_PASSWORD_FIELD_EMPTY("Identity password field is empty"),

    IDENTITY_FIO_OBJECT_EMPTY("Identity fio object is empty"),
    IDENTITY_FIRSTNAME_FIELD_EMPTY("Identity first name field is empty"),
    IDENTITY_LASTNAME_FIELD_EMPTY("Identity last name field is empty"),
    IDENTITY_PATRONYMIC_FIELD_EMPTY("Identity patronymic field is empty"),
    IDENTITY_COMPANY_FIELD_EMPTY("Identity company field is empty");

    private final String message;
}
