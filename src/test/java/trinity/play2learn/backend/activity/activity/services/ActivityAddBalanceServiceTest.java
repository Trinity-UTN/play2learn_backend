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
class ActivityAddBalanceServiceTest {

    private static final Double INITIAL_BALANCE = 100.0;
    private static final Double AMOUNT_TO_ADD = 50.0;
    private static final Double EXPECTED_BALANCE = 150.0;

    @Mock
    private IActivityRepository activityRepository;

    private ActivityAddBalanceService activityAddBalanceService;

    @BeforeEach
    void setUp() {
        activityAddBalanceService = new ActivityAddBalanceService(activityRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("Given activity with balance When adding amount Then increments balance and persists")
        void whenAddingBalance_incrementsBalance() {
            // Given
            Activity activity = ActivityTestMother.activityWithBalance(
                ActivityTestMother.ACTIVITY_ID, 
                INITIAL_BALANCE
            );

            when(activityRepository.save(any(Activity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            activityAddBalanceService.execute(activity, AMOUNT_TO_ADD);

            // Then
            ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
            verify(activityRepository).save(activityCaptor.capture());
            Activity savedActivity = activityCaptor.getValue();

            assertThat(savedActivity)
                .extracting(Activity::getActualBalance)
                .isEqualTo(EXPECTED_BALANCE);
        }

        @Test
        @DisplayName("Given activity with zero balance When adding amount Then sets balance to amount")
        void whenAddingToZeroBalance_setsBalanceToAmount() {
            // Given
            Activity activity = ActivityTestMother.activityWithBalance(
                ActivityTestMother.ACTIVITY_ID, 
                0.0
            );

            when(activityRepository.save(any(Activity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            activityAddBalanceService.execute(activity, AMOUNT_TO_ADD);

            // Then
            assertThat(activity)
                .extracting(Activity::getActualBalance)
                .isEqualTo(AMOUNT_TO_ADD);
            verify(activityRepository).save(activity);
        }
    }
}

