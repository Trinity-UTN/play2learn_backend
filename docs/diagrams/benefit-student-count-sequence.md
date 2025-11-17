# Diagrama de Secuencia: Conteo de Beneficios por Estado (cu89)

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor ST as Estudiante (ROLE_STUDENT)
    participant JA as JwtSessionAspect
    participant JS as IJwtService
    participant UES as UserExistsByEmailService
    participant UExS as UserExistsService
    participant UR as IUserRepository
    participant C as BenefitStudentCountController
    participant SUAR as SessionUserArgumentResolver
    participant UGS as IUserGetByEmailService
    participant User as User
    participant Svc as BenefitStudentCountService
    participant SGES as StudentGetByEmailService
    participant SR as IStudentRepository
    participant Student as Student
    participant BGS as BenefitGetByStudentService
    participant SGS as SubjectGetByStudentService
    participant SubRepo as ISubjectRepository
    participant BR as IBenefitRepository
    participant BPR as IBenefitPurchaseRepository
    participant BLPS as BenefitGetLastPurchaseService
    participant BP as BenefitPurchase
    participant Benefit as Benefit
    participant BPLBS as BenefitGetPurchasesLeftByStudentService
    participant BM as BenefitMapper
    participant SM as SuccessfulMessages
    participant RF as ResponseFactory

    ST->>C: GET /benefits/student/count<br/>Header: Authorization: Bearer {jwt}
    Note right of C: @GetMapping("/count")<br/>@SessionRequired(roles = {ROLE_STUDENT})

    Note over JA: Interceptor ejecutado antes del método
    JA->>JA: validateJwt(joinPoint, sessionRequired)
    JA->>JA: RequestContextHolder.getRequestAttributes()
    alt Solicitud no HTTP
        JA-->>ST: 500 Internal Server Error<br/>(NOT_HTTP)
    else Petición HTTP
        JA->>JA: attrs.getRequest()
    end
    JA->>JA: request.getHeader("Authorization")
    alt Encabezado ausente o inválido
        JA-->>ST: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Encabezado Bearer
        JA->>JA: jwt = authHeader.substring(7)
    end
    JA->>JS: isTokenExpired(jwt)
    alt Token expirado
        JS-->>JA: JwtException
        JA-->>ST: 401 Unauthorized<br/>(TOKEN_EXPIRED)
    else Token vigente
        JS-->>JA: false
    end
    JA->>JS: extractUsername(jwt)
    alt Firma inválida
        JS-->>JA: JwtException
        JA-->>ST: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
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
        JA-->>ST: 401 Unauthorized
    else Usuario activo
        UExS-->>UES: true
        UES-->>JA: ok
    end
    JA->>JS: extractRole(jwt)
    JS-->>JA: role
    JA->>JA: requiredRoles = Arrays.asList(sessionRequired.roles())
    alt Rol no permitido
        JA-->>ST: 401 Unauthorized<br/>(requiredRoles)
    else Rol autorizado
        JA-->>C: Token válido
    end

    C->>SUAR: resolver @SessionUser(User)
    SUAR->>SUAR: webRequest.getNativeRequest(HttpServletRequest.class)
    SUAR->>SUAR: request.getHeader("Authorization")
    alt Encabezado ausente o inválido
        SUAR-->>ST: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Encabezado Bearer
        SUAR->>SUAR: jwt = authHeader.substring(7)
    end
    SUAR->>JS: extractUsername(jwt)
    alt Token inválido
        JS-->>SUAR: Exception
        SUAR-->>ST: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Email válido
        JS-->>SUAR: email
    end
    SUAR->>UGS: findUserByEmail(email)
    UGS->>UR: findByEmailAndDeletedAtIsNull(email)
    UR-->>UGS: Optional<User>
    alt Usuario no encontrado
        UGS-->>SUAR: UnauthorizedException
        SUAR-->>ST: 401 Unauthorized
    else Usuario activo
        UGS-->>SUAR: User
        SUAR-->>C: User
    end

    C->>Svc: cu89CountByStudentState(user)
    Svc->>SGES: getByEmail(user.email)
    SGES->>SR: findByUserEmailAndDeletedAtIsNull(user.email)
    SR-->>SGES: Optional<Student>
    alt Estudiante no encontrado
        SGES-->>Svc: NotFoundException
        Svc-->>C: NotFoundException
        C-->>ST: 404 Not Found
    else Estudiante encontrado
        SGES-->>Svc: Student
    end

    Svc->>BGS: getByStudent(student)
    BGS->>SGS: getByStudent(student)
    SGS->>SubRepo: findAllByStudentsContainingAndDeletedAtIsNull(student)
    SubRepo-->>SGS: List<Subject>
    SGS-->>BGS: List<Subject>
    BGS->>BR: findBySubjectIn(subjects)
    BR-->>BGS: List<Benefit>
    BGS-->>Svc: List<Benefit>

    Svc->>BPR: countByStudentAndState(student, BenefitPurchaseState.USED)
    BPR-->>Svc: usedCount

    loop Beneficios del estudiante
        Svc->>BLPS: getLastPurchase(benefit, student)
        BLPS->>BPR: findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student)
        BPR-->>BLPS: Optional<BenefitPurchase>
        BLPS-->>Svc: Optional<BenefitPurchase>

        Svc->>Benefit: isExpired()
        alt Beneficio expirado
            Svc->>Svc: expired.add(benefit)
        else Beneficio vigente
            alt Compra previa encontrada
                Svc->>BP: isPurchased()
                alt Estado PURCHASED
                    Svc->>Svc: purchased.add(benefit)
                else No PURCHASED
                    Svc->>BP: isUseRequested()
                    alt Estado USE_REQUESTED
                        Svc->>Svc: use_requested.add(benefit)
                    else Evaluar cupos disponibles
                        Svc->>Benefit: getPurchasesLeft()
                        alt Cupo global disponible (null o > 0)
                            Svc->>BPLBS: getPurchasesLeftByStudent(benefit, student)
                            alt Beneficio sin límite por estudiante
                                BPLBS-->>Svc: null
                            else Con límite por estudiante
                                BPLBS->>BPR: findByBenefitAndStudent(benefit, student)
                                BPR-->>BPLBS: List<BenefitPurchase>
                                BPLBS-->>Svc: purchasesLeftByStudent
                            end
                            alt Cupo por estudiante disponible (null o > 0)
                                Svc->>Svc: available.add(benefit)
                            else Sin cupos para el estudiante
                                Svc->>Svc: // no se agrega a disponibles
                            end
                        else Cupo global agotado
                            Svc->>Svc: // no se agrega a disponibles
                        end
                    end
                end
            else Sin compras previas
                Svc->>Benefit: getPurchasesLeft()
                alt Cupo global disponible (null o > 0)
                    Svc->>BPLBS: getPurchasesLeftByStudent(benefit, student)
                    alt Beneficio sin límite por estudiante
                        BPLBS-->>Svc: null
                    else Con límite por estudiante
                        BPLBS->>BPR: findByBenefitAndStudent(benefit, student)
                        BPR-->>BPLBS: List<BenefitPurchase>
                        BPLBS-->>Svc: purchasesLeftByStudent
                    end
                    alt Cupo por estudiante disponible (null o > 0)
                        Svc->>Svc: available.add(benefit)
                    else Sin cupos para el estudiante
                        Svc->>Svc: // no se agrega a disponibles
                    end
                else Cupo global agotado
                    Svc->>Svc: // no se agrega a disponibles
                end
            end
        end
    end

    Svc->>BM: tCountResponseDto(available.size(), purchased.size(), use_requested.size(), usedCount, expired.size())
    BM-->>Svc: BenefitStudentCountResponseDto
    Svc-->>C: BenefitStudentCountResponseDto

    C->>SM: okSuccessfully()
    SM-->>C: "OK"
    C->>RF: ok(BenefitStudentCountResponseDto,<br/>"OK")
    RF->>RF: BaseResponse.builder()...
    RF-->>C: ResponseEntity<BaseResponse<BenefitStudentCountResponseDto>>
    C-->>ST: 200 OK<br/>BaseResponse{data: BenefitStudentCountResponseDto, message:"OK", errors:null, timestamp}

    note over Svc: finCU()

    classDef controller fill:#2196f3,stroke:#fff,color:#fff
    classDef service fill:#4caf50,stroke:#fff,color:#fff
    classDef repository fill:#ff9800,stroke:#fff,color:#fff
    classDef entity fill:#9e9e9e,stroke:#fff,color:#fff
    classDef mapper fill:#9c27b0,stroke:#fff,color:#fff

    class C controller
    class JA,JS,UES,UExS,SUAR,UGS,Svc,SGES,BGS,SGS,BLPS,BPLBS,SM,RF service
    class UR,SR,SubRepo,BR,BPR repository
    class User,Student,Benefit,BP entity
    class BM mapper
```


