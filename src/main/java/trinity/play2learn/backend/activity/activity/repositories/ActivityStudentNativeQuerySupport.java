package trinity.play2learn.backend.activity.activity.repositories;

import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

final class ActivityStudentNativeQuerySupport {

    static final int STATE_APPROVED = ActivityCompletedState.APPROVED.ordinal();
    static final int STATE_PENDING = ActivityCompletedState.PENDING.ordinal();

    static final String LATEST_COMPLETION_CTE = """
            WITH latest_completion AS (
                SELECT DISTINCT ON (activity_id)
                    activity_id,
                    id AS completion_id,
                    state,
                    remaining_attempts
                FROM activity_completed
                WHERE student_id = :studentId
                ORDER BY activity_id, completed_at DESC NULLS LAST, id DESC
            )
            """;

    private ActivityStudentNativeQuerySupport() {
    }
}
