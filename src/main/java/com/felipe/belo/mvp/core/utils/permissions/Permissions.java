package com.felipe.belo.mvp.core.utils.permissions;

/**
 * System-wide permission identifiers used to authorize actions.
 */
public enum Permissions {
    // Role permissions
    /** Read a role. */
    ROLE_READ,
    /** Create a new role. */
    ROLE_CREATE,
    /** Update an existing role. */
    ROLE_UPDATE,
    /** Delete a role. */
    ROLE_DELETE,
    /** List roles. */
    ROLE_LIST,

    // User permissions
    /** Read a user. */
    USER_READ,
    /** Create a new user. */
    USER_CREATE,
    /** Update an existing user. */
    USER_UPDATE,
    /** Delete a user. */
    USER_DELETE,
    /** List users. */
    USER_LIST
}
