package mops.businesslogic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPermission {

    /**
     * read permission flag.
     */
    private final boolean read;
    /**
     * write permission flag.
     */
    private final boolean write;
    /**
     * delete permission flag.
     */
    private final boolean delete;
}
