package mops.businesslogic.group;

import mops.persistence.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    GroupRepository groupRepository;
    @Mock
    RestTemplate restTemplate;

    GroupService groupService;

    @BeforeEach
    void setup() {
        groupService = new GroupServiceProdImpl(groupRepository, restTemplate);
    }
}
