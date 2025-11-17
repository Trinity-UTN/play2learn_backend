package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectAddBalanceService {
    
    public Subject execute (Subject subject, Double amount); 
    
}
