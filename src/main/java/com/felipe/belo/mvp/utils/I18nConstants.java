package com.felipe.belo.mvp.utils;

/**
 * Centralized i18n message keys used across the application.
 * <p>
 * These constants map to entries in {@code src/main/resources/i18n/messages*.properties}.
 */
public final class I18nConstants {

    public static final String MESSAGE_INTERNAL_SERVER_ERROR = "internal.error";

    /** Utility class; do not instantiate. */
    private I18nConstants() {}

    public static final String  MESSAGE_USER_FORBIDDEN = "user.forbidden";
    /** User not found error key. */
    public static final String MESSAGE_USER_NOT_FOUND = "user.not.found";
    /** User already exists error key. */
    public static final String MESSAGE_USER_EXISTS = "user.exists";
    /** User name length validation key. */
    public static final String MESSAGE_USER_NAME_LENGTH = "user.name.length";
    /** User name required validation key. */
    public static final String MESSAGE_USER_NAME_REQUIRED = "user.name.required";
    /** User e-mail already exists error key. */
    public static final String MESSAGE_USER_EMAIL_EXISTS = "user.email.exists";
    /** User e-mail invalid validation key. */
    public static final String MESSAGE_USER_EMAIL_INVALID = "user.email.invalid";
    /** User e-mail required validation key. */
    public static final String MESSAGE_USER_EMAIL_REQUIRED = "user.email.required";
    /** User e-mail length validation key. */
    public static final String MESSAGE_USER_EMAIL_LENGTH = "user.email.length";
    /** User password invalid validation key. */
    public static final String MESSAGE_USER_PASSWORD_INVALID = "user.password.invalid";
    /** User password length validation key. */
    public static final String MESSAGE_USER_PASSWORD_LENGTH = "user.password.length";
    /** User password required validation key. */
    public static final String MESSAGE_USER_PASSWORD_REQUIRED = "user.password.required";
    /** Role name length validation key. */
    public static final String MESSAGE_ROLE_NAME_LENGTH = "role.name.length";
    /** Role name already exists error key. */
    public static final String MESSAGE_ROLE_NAME_EXISTS = "role.name.exists";
    /** Role not found error key. */
    public static final String MESSAGE_ROLE_NAME_NOT_FOUND = "role.name.not.found";
}