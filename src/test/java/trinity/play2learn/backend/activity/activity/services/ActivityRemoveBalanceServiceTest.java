package trinity.play2learn.backend.activity.activity.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import trinity.play2learn.backend.activity.activity.ActivityTestMother;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;

@ExtendWith(MockitoExtension.class)
class ActivityRemoveBalanceServiceTest {

    private static final Double INITIAL_BALANCE = 100.0;
    private static final Double AMOUNT_TO_REMOVE = 30.0;
    private static final Double EXPECTED_BALANCE = 70.0;

    @Mock
    private IActivityRepository activityRepository;

    private ActivityRemoveBalanceService activityRemoveBalanceService;

    @BeforeEach
    void setUp() {
        activityRemoveBalanceService = new ActivityRemoveBalanceService(activityRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given activity with balance When removing amount Then decrements balance and persists")
        void whenRemovingBalance_decrementsBalance() {
            // Given
            Activity activity = ActivityTestMother.activityWithBalance(
                ActivityTestMother.ACTIVITY_ID, 
                INITIAL_BALANCE
            );

            when(activityRepository.save(any(Activity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            activityRemoveBalanceService.execute(activity, AMOUNT_TO_REMOVE);

            // Then
            ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
            verify(activityRepository).save(activityCaptor.capture());
            Activity savedActivity = activityCaptor.getValue();

            assertThat(savedActivity)
                .extracting(Activity::getActualBalance)
                .isEqualTo(EXPECTED_BALANCE);
        }

        @Test
        @DisplayName("Given activity with balance When removing full amount Then sets balance to zero")
        void whenRemovingFullBalance_setsBalanceToZero() {
            // Given
            Activity activity = ActivityTestMother.activityWithBalance(
                ActivityTestMother.ACTIVITY_ID, 
                INITIAL_BALANCE
            );

            when(activityRepository.save(any(Activity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            activityRemoveBalanceService.execute(activity, INITIAL_BALANCE);

            // Then
            assertThat(activity)
                .extracting(Activity::getActualBalance)
                .isEqualTo(0.0);
            verify(activityRepository).save(activity);
        }
    }
}

