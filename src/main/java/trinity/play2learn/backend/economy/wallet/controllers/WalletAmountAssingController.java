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
import trinity.play2learn.backend.economy.transaction.models.Transaction;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/wallet/test")
public class WalletAmountAssingController {

    private final ITransactionGenerateService generateTransactionService;

    private final IStudentGetByIdService studentGetByIdService;

    private final ISubjectGetByIdService subjectGetByIdService;

    @PostMapping("/{id}")
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<BaseResponse<Void>> asign(
        @PathVariable Long id
    ) {

        Student student = studentGetByIdService.findById(id);

        Subject subject = subjectGetByIdService.findById(3L);

        generateTransactionService.generate(
            TypeTransaction.RECOMPENSA, 
            25000.0, 
            "Recompensa a estudiante", 
            TransactionActor.SISTEMA, 
            TransactionActor.ESTUDIANTE, 
            student.getWallet(), 
            subject
        );

        return ResponseFactory.noContent(
            "Asignado"
        );
    }
    
}

