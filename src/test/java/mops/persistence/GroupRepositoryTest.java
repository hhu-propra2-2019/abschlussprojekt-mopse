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
    public void saveTest() {
        Group group = Group.builder()
                .groupId(new UUID(0, 123))
                .member("Segelzwerg", "admin")
                .name("Root")
                .build();
        Group save = groupRepository.save(group);

        assertThat(group).isEqualTo(save);

    }

    @Test
    public void findByUserTest() {
        Group group = Group.builder()
                .groupId(new UUID(0, 123))
                .member("Segelzwerg", "admin")
                .name("Root")
                .build();
        groupRepository.save(group);

        List<Group> groups = groupRepository.findByUser("Segelzwergg");

        assertThat(groups).containsExactly(group);
    }

    @Test
    public void findMultipleGroupsTest() {
        Group group1 = Group.builder()
                .groupId(new UUID(0, 123))
                .member("Segelzwerg", "admin")
                .name("Root")
                .build();

        Group group2 = Group.builder()
                .groupId(new UUID(0, 124))
                .member("Segelzwerg", "admin")
                .name("FirstLevel")
                .build();

        Group group3 = Group.builder()
                .groupId(new UUID(0, 125))
                .member("Segelzwerg", "admin")
                .name("2ndLeve")
                .build();

        Group group4 = Group.builder()
                .groupId(new UUID(0, 126))
                .member("iTitus", "admin")
                .name("Wrong")
                .build();

        List<Group> groups= List.of(group1, group2, group3, group4);

        groupRepository.saveAll(groups);

        List<Group> groupsOfUser1 = groupRepository.findByUser("Segelzwerg");
        List<Group> groupsOfUser2 = groupRepository.findByUser("iTitus");

        assertThat(groupsOfUser1).containsExactly(group1, group2, group3);
        assertThat(groupsOfUser2).containsExactly(group4);

    }
}
