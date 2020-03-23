package mops.businesslogic.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    RestTemplate restTemplate;

    GroupService groupService;

    @BeforeEach
    void setup() {
        groupService = new GroupServiceProdImpl(restTemplate);
    }
}
