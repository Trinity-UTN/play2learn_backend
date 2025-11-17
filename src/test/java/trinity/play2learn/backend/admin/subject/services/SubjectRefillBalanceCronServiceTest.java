package trinity.play2learn.backend.admin.subject.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectRefillBalanceService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubjectRefillBalanceCronServiceTest {

    @Mock
    private ISubjectRefillBalanceService subjectRefillBalanceService;

    @Test
    @DisplayName("When cron executes Then delegates to refill balance service")
    void executeInvokesRefillBalanceService() {
        SubjectRefillBalanceCronService cronService = new SubjectRefillBalanceCronService(subjectRefillBalanceService);

        cronService.execute();

        verify(subjectRefillBalanceService).cu58RefillBalance();
    }
}

