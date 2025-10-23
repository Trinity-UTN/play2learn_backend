package trinity.play2learn.backend.activity.activity.dtos.activityStudent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityStudentCountResponseDto {
    
    private int available;
    private int approved;
    private int disapproved;
    private int expired;
}
