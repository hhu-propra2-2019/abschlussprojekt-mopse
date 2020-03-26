package mops.persistence;

import mops.persistence.group.Group;
import mops.util.AuditingDbContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AuditingDbContext
@DataJdbcTest
class GroupRepositoryTest {

    @Autowired
    GroupRepository groupRepository;

    @Test
    public void findByUserTest() {
        Group group = Group.builder()
                .id(new UUID(0, 123))
                .member("Segelzwerg", "admin")
                .name("Root")
                .build();
        groupRepository.save(group);

        List<Group> groups = groupRepository.findByUser("Segelzwergg");

        assertThat(groups).containsExactly(group);
    }

}
