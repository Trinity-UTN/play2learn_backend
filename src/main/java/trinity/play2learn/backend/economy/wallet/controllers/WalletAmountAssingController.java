package trinity.play2learn.backend.economy.wallet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.economy.transaccion.models.ActorTransaccion;
import trinity.play2learn.backend.economy.transaccion.models.Transaccion;
import trinity.play2learn.backend.economy.transaccion.models.TypeTransaccion;
import trinity.play2learn.backend.economy.transaccion.services.interfaces.ITransaccionGenerateService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/wallet/test")
public class WalletAmountAssingController {

    private final ITransaccionGenerateService generateTransaccionService;

    private final IStudentGetByIdService studentGetByIdService;

    private final ISubjectGetByIdService subjectGetByIdService;

    @PostMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<Void>> asign(
        @PathVariable Long id
    ) {

        Student student = studentGetByIdService.findById(id);

        Subject subject = subjectGetByIdService.findById(3L);

        Transaccion transaccion = generateTransaccionService.generate(
            TypeTransaccion.RECOMPENSA, 
            1000.0, 
            "Recompensa a estudiante", 
            ActorTransaccion.SISTEMA, 
            ActorTransaccion.ESTUDIANTE, 
            student.getWallet(), 
            subject
        );

        return ResponseFactory.noContent(
            "Asignado"
        );
    }
    
}

