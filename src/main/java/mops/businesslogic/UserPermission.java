package mops.businesslogic;

import lombok.Value;

/**
 * Wrapper for permission of one user in one directory.
 */
@Value
// @Value automatically makes all fields `private final` which CheckStyle and PMD don't see
@SuppressWarnings({ "checkstyle:VisibilityModifier", "PMD.DefaultPackage" })
public class UserPermission {

    /**
     * read permission flag.
     */
    boolean read;
    /**
     * write permission flag.
     */
    boolean write;
    /**
     * delete permission flag.
     */
    boolean delete;

}
