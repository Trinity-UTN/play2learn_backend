# Diagrama de Secuencia: Eliminación de Beneficio (cu94)

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor T as Docente (ROLE_TEACHER)
    participant JA as JwtSessionAspect
    participant JS as IJwtService
    participant UES as UserExistsByEmailService
    participant UExS as UserExistsService
    participant UR as IUserRepository
    participant SUAR as SessionUserArgumentResolver
    participant UGS as IUserGetByEmailService
    participant User as User
    participant C as BenefitDeleteController
    participant Svc as BenefitDeleteService
    participant TGES as TeacherGetByEmailService
    participant TR as ITeacherRepository
    participant BGIS as BenefitGetByIdService
    participant BR as IBenefitRepository
    participant Benefit as Benefit
    participant BPR as IBenefitPurchaseRepository
    participant TGSvc as TransactionGenerateService
    participant RTS as ReembolsoTransactionService
    participant RFS as IReserveFindLastService
    participant RR as IReserveRepository
    participant Reserve as Reserve
    participant TM as TransactionMapper
    participant TRp as ITransactionRepository
    participant WAAS as IWalletAddAmountService
    participant WR as IWalletRepository
    participant RMS as IReserveModifyService
    participant RF as ResponseFactory

    T->>C: DELETE /benefits/teacher/{id}<br/>Header: Authorization: Bearer {jwt}
    Note right of C: @DeleteMapping("/{id}")<br/>@SessionRequired(roles = {ROLE_TEACHER})

    Note over JA: Interceptor ejecutado antes del método
    JA->>JA: validateJwt(joinPoint, sessionRequired)
    JA->>JA: RequestContextHolder.getRequestAttributes()
    alt Solicitud no HTTP
        JA-->>T: 500 Internal Server Error<br/>(NOT_HTTP)
    else Petición HTTP
        JA->>JA: attrs.getRequest()
    end
    JA->>JA: request.getHeader("Authorization")
    alt Encabezado ausente o inválido
        JA-->>T: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Encabezado Bearer
        JA->>JA: jwt = authHeader.substring(7)
    end
    JA->>JS: isTokenExpired(jwt)
    alt Token expirado
        JS-->>JA: JwtException
        JA-->>T: 401 Unauthorized<br/>(TOKEN_EXPIRED)
    else Token vigente
        JS-->>JA: false
    end
    JA->>JS: extractUsername(jwt)
    alt Firma inválida
        JS-->>JA: JwtException
        JA-->>T: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Email válido
        JS-->>JA: email
    end
    JA->>UES: validateIfUserIsActive(email)
    UES->>UExS: validate(email)
    UExS->>UR: existsByEmailAndDeletedAtIsNull(email)
    UR-->>UExS: boolean
    alt Usuario inactivo
        UExS-->>UES: false
        UES-->>JA: UnauthorizedException
        JA-->>T: 401 Unauthorized
    else Usuario activo
        UExS-->>UES: true
        UES-->>JA: ok
    end
    JA->>JS: extractRole(jwt)
    JS-->>JA: role
    JA->>JA: requiredRoles = Arrays.asList(sessionRequired.roles())
    alt Rol no permitido
        JA-->>T: 401 Unauthorized<br/>(requiredRoles)
    else Rol autorizado
        JA-->>C: Token válido
    end

    C->>SUAR: resolver @SessionUser(User)
    SUAR->>SUAR: webRequest.getNativeRequest(HttpServletRequest.class)
    SUAR->>SUAR: request.getHeader("Authorization")
    alt Encabezado ausente o inválido
        SUAR-->>T: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Encabezado Bearer
        SUAR->>SUAR: jwt = authHeader.substring(7)
    end
    SUAR->>JS: extractUsername(jwt)
    alt Token inválido
        JS-->>SUAR: Exception
        SUAR-->>T: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Email válido
        JS-->>SUAR: email
    end
    SUAR->>UGS: findUserByEmail(email)
    UGS->>UR: findByEmailAndDeletedAtIsNull(email)
    UR-->>UGS: Optional<User>
    alt Usuario no encontrado
        UGS-->>SUAR: UnauthorizedException
        SUAR-->>T: 401 Unauthorized
    else Usuario activo
        UGS-->>SUAR: User
        SUAR-->>C: User
    end

    C->>Svc: cu94DeleteBenefit(user, id)
    Svc->>TGES: getByEmail(user.email)
    TGES->>TR: findByUserEmailAndDeletedAtIsNull(user.email)
    TR-->>TGES: Optional<Teacher>
    alt Docente no encontrado
        TGES-->>Svc: NotFoundException
        Svc-->>C: NotFoundException
        C-->>T: 404 Not Found
    else Docente encontrado
        TGES-->>Svc: Teacher
    end
    opt Continuar con docente válido
        Svc->>BGIS: getById(id)
        BGIS->>BR: findByIdAndDeletedAtIsNull(id)
        BR-->>BGIS: Optional<Benefit>
        alt Beneficio no encontrado
            BGIS-->>Svc: NotFoundException
            Svc-->>C: NotFoundException
            C-->>T: 404 Not Found
        else Beneficio encontrado
            BGIS-->>Svc: Benefit
        end
    end
    opt Validar pertenencia del beneficio
        Svc->>Benefit: getSubject().getTeacher()
        alt Beneficio no pertenece al docente
            Svc-->>C: ConflictException("No es posible eliminar...")
            C-->>T: 409 Conflict
        else Beneficio del docente
            Note over Svc,Benefit: Validación superada
        end
    end
    Svc->>Benefit: isExpired()
    alt Beneficio expirado
        Benefit-->>Svc: true
        Note over Svc: No se reembolsa porque el beneficio ya expiró
    else Beneficio vigente
        Benefit-->>Svc: false
        Svc->>BPR: findByBenefit(benefit)
        BPR-->>Svc: List<BenefitPurchase>
        loop benefitPurchases
            Svc->>Svc: evaluar estado (benefitPurchase.getState())
            alt Estado distinto de USED
                Svc->>TGSvc: generate(REEMBOLSO, Double.valueOf(benefit.getCost()), "Reembolso de beneficio", SISTEMA, ESTUDIANTE, benefitPurchase.student.wallet, null, null, benefit, null, null, null)
                TGSvc->>TGSvc: strategy = strategies.get("REEMBOLSO")
                alt Estrategia no soportada
                    TGSvc-->>Svc: ConflictException
                    Svc-->>C: ConflictException
                    C-->>T: 409 Conflict
                else Estrategia disponible
                    TGSvc->>RTS: execute(Double.valueOf(benefit.getCost()), "Reembolso de beneficio", SISTEMA, ESTUDIANTE, benefitPurchase.student.wallet, null, null, benefit, null, null, null)
                    RTS->>RFS: get()
                    RFS->>RR: findFirstByOrderByCreatedAtDesc()
                    RR-->>RFS: Optional<Reserve>
                    alt Reserva no existente
                        RFS-->>RTS: NotFoundException
                        RTS-->>TGSvc: NotFoundException
                        TGSvc-->>Svc: NotFoundException
                        Svc-->>C: NotFoundException
                        C-->>T: 404 Not Found
                    else Reserva recuperada
                        RFS-->>RTS: Reserve
                        RTS->>TM: toModel(Double.valueOf(benefit.getCost()), "Reembolso de beneficio", SISTEMA, ESTUDIANTE, benefitPurchase.student.wallet, null, null, benefit, null, null, null, reserve)
                        TM-->>RTS: Transaction
                        RTS->>TRp: save(transaction)
                        TRp-->>RTS: Transaction
                        RTS->>WAAS: execute(benefitPurchase.student.wallet, Double.valueOf(benefit.getCost()))
                        WAAS->>WR: save(wallet)
                        WR-->>WAAS: Wallet
                        WAAS-->>RTS: Wallet
                        RTS->>RMS: moveToCirculation(Double.valueOf(benefit.getCost()), reserve)
                        RMS->>RR: save(reserve)
                        RR-->>RMS: Reserve
                        RMS-->>RTS: Reserve
                        RTS-->>TGSvc: Transaction
                        TGSvc-->>Svc: Transaction
                    end
                end
            else Estado USED
                Note over Svc: No se genera reembolso
            end
        end
    end

    Svc->>Svc: benefit.setDeletedAt(LocalDateTime.now())
    Svc->>BR: save(benefit)
    BR-->>Svc: Benefit
    Svc-->>C: void
    C->>RF: noContent("Beneficio eliminado con exito")
    RF-->>C: ResponseEntity<BaseResponse<Void>>
    C-->>T: 204 No Content<br/>BaseResponse<Void> { data:null, message:"Beneficio eliminado con exito", errors:null, timestamp }

    note over Svc: finCU()

    classDef controller fill:#2196f3,stroke:#fff,color:#fff
    classDef service fill:#4caf50,stroke:#fff,color:#fff
    classDef repository fill:#ff9800,stroke:#fff,color:#fff
    classDef entity fill:#9e9e9e,stroke:#fff,color:#fff
    classDef mapper fill:#9c27b0,stroke:#fff,color:#fff
    classDef strategy fill:#f44336,stroke:#fff,color:#fff

    class C controller
    class JA,JS,UES,UExS,SUAR,UGS,Svc,TGES,BGIS,TGSvc,RFS,WAAS,RMS,RF service
    class TR,BR,BPR,RR,TRp,WR repository
    class User,Benefit,Reserve entity
    class TM mapper
    class RTS strategy
```


