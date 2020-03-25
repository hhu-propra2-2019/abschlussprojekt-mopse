package mops.businesslogic.security;

import lombok.Value;

/**
 * Wrapper for permission of one user in one directory.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class UserPermission {

    /**
     * Read permission flag.
     */
    boolean read;
    /**
     * Write permission flag.
     */
    boolean write;
    /**
     * Delete permission flag.
     */
    boolean delete;

}
