package trinity.play2learn.backend.admin.classes.services.interfaces;

import trinity.play2learn.backend.admin.classes.dtos.ClassRequestDto;
import trinity.play2learn.backend.admin.classes.dtos.ClassResponseDto;

public interface IClassRegisterService {
    
    public ClassResponseDto cu6RegisterClass(ClassRequestDto classRequestDto);
    
}
