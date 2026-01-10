package com.felipe.belo.mvp.core.utils;

import org.springframework.modulith.NamedInterface;

/**
 * Centralized i18n message keys used across the application.
 * <p>
 * These constants map to entries in {@code src/main/resources/i18n/messages*.properties}.
 */
@NamedInterface
public final class I18nConstants {

    /** Generic internal server error key. */
    public static final String MESSAGE_INTERNAL_SERVER_ERROR = "internal.error";

    /** Utility class; do not instantiate. */
    private I18nConstants() {}

    /** Forbidden action key. */
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

    // Swagger/OpenAPI descriptions
    /** Swagger tag for role endpoints. */
    public static final String SWAGGER_ROLE_TAG = "swagger.role.tag";
    /** Swagger summary for role creation. */
    public static final String SWAGGER_ROLE_CREATE_SUMMARY = "swagger.role.create.summary";
    /** Swagger description for role creation. */
    public static final String SWAGGER_ROLE_CREATE_DESC = "swagger.role.create.desc";
    /** Swagger summary for role listing. */
    public static final String SWAGGER_ROLE_LIST_SUMMARY = "swagger.role.list.summary";
    /** Swagger description for role listing. */
    public static final String SWAGGER_ROLE_LIST_DESC = "swagger.role.list.desc";
    /** Swagger summary for role get. */
    public static final String SWAGGER_ROLE_GET_SUMMARY = "swagger.role.get.summary";
    /** Swagger description for role get. */
    public static final String SWAGGER_ROLE_GET_DESC = "swagger.role.get.desc";
    /** Swagger summary for role update. */
    public static final String SWAGGER_ROLE_UPDATE_SUMMARY = "swagger.role.update.summary";
    /** Swagger description for role update. */
    public static final String SWAGGER_ROLE_UPDATE_DESC = "swagger.role.update.desc";
    /** Swagger summary for role delete. */
    public static final String SWAGGER_ROLE_DELETE_SUMMARY = "swagger.role.delete.summary";
    /** Swagger description for role delete. */
    public static final String SWAGGER_ROLE_DELETE_DESC = "swagger.role.delete.desc";

    /** Swagger tag for user endpoints. */
    public static final String SWAGGER_USER_TAG = "swagger.user.tag";
    /** Swagger summary for user creation. */
    public static final String SWAGGER_USER_CREATE_SUMMARY = "swagger.user.create.summary";
    /** Swagger description for user creation. */
    public static final String SWAGGER_USER_CREATE_DESC = "swagger.user.create.desc";
    /** Swagger summary for user listing. */
    public static final String SWAGGER_USER_LIST_SUMMARY = "swagger.user.list.summary";
    /** Swagger description for user listing. */
    public static final String SWAGGER_USER_LIST_DESC = "swagger.user.list.desc";
    /** Swagger summary for user get. */
    public static final String SWAGGER_USER_GET_SUMMARY = "swagger.user.get.summary";
    /** Swagger description for user get. */
    public static final String SWAGGER_USER_GET_DESC = "swagger.user.get.desc";
    /** Swagger summary for user update. */
    public static final String SWAGGER_USER_UPDATE_SUMMARY = "swagger.user.update.summary";
    /** Swagger description for user update. */
    public static final String SWAGGER_USER_UPDATE_DESC = "swagger.user.update.desc";

    /** Swagger tag for auth endpoints. */
    public static final String SWAGGER_AUTH_TAG = "swagger.auth.tag";
    /** Swagger summary for login. */
    public static final String SWAGGER_AUTH_LOGIN_SUMMARY = "swagger.auth.login.summary";
    /** Swagger description for login. */
    public static final String SWAGGER_AUTH_LOGIN_DESC = "swagger.auth.login.desc";
    /** Swagger summary for refresh. */
    public static final String SWAGGER_AUTH_REFRESH_SUMMARY = "swagger.auth.refresh.summary";
    /** Swagger description for refresh. */
    public static final String SWAGGER_AUTH_REFRESH_DESC = "swagger.auth.refresh.desc";

    /** Swagger tag for permission endpoints. */
    public static final String SWAGGER_PERMISSIONS_TAG = "swagger.permissions.tag";
    /** Swagger summary for permission listing. */
    public static final String SWAGGER_PERMISSIONS_LIST_SUMMARY = "swagger.permissions.list.summary";
    /** Swagger description for permission listing. */
    public static final String SWAGGER_PERMISSIONS_LIST_DESC = "swagger.permissions.list.desc";
}