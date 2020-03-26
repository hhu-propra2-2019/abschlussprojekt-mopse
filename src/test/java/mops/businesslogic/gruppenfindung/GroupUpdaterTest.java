package mops.businesslogic.gruppenfindung;

import mops.businesslogic.event.LatestEventIdService;
import mops.businesslogic.group.GroupService;
import mops.exception.MopsException;
import mops.persistence.event.LatestEventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroupUpdaterTest {

    // TODO: test if the DTO objects are converted to our data objects correctly (adding, deleting etc.)

    @Mock
    GruppenfindungsService gruppenfindungsService;
    @Mock
    LatestEventIdService latestEventIdService;
    @Mock
    GroupService groupService;

    GroupUpdater groupUpdater;

    @BeforeEach
    void setup(){
        groupUpdater = new GroupUpdater(gruppenfindungsService, latestEventIdService, groupService);
    }

    @Test
    void latestEventIdGetsUpdated() throws MopsException {
        LatestEventId latestEventId = LatestEventId.of();
        given(latestEventIdService.getLatestEventId()).willReturn(latestEventId);

        UpdatedGroupsDTO updatedGroups = new UpdatedGroupsDTO();
        updatedGroups.setEventId(20L);
        updatedGroups.setGroupDAOs(List.of());
        given(gruppenfindungsService.getUpdatedGroups(any())).willReturn(updatedGroups);

        groupUpdater.updateDatabase();

        assertThat(latestEventId.getEventId()).isEqualTo(20L);
    }
}
