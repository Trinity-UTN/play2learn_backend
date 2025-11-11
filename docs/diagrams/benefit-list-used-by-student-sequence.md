# Diagrama de Secuencia: Beneficios Usados por Estudiante (cu93)

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
    participant SUAR as SessionUserArgumentResolver
    participant UGS as IUserGetByEmailService
    participant User as User
    participant C as BenefitListUsedByStudentController
    participant Svc as BenefitListUsedByStudentService
    participant SGES as StudentGetByEmailService
    participant SR as IStudentRepository
    participant BPR as IBenefitPurchaseRepository
    participant BPM as BenefitPurchaseMapper
    participant SM as SuccessfulMessages
    participant RF as ResponseFactory

    ST->>C: GET /benefits/student/used<br/>Header: Authorization: Bearer {jwt}
    Note right of C: @GetMapping("/used")<br/>@SessionRequired(roles = {ROLE_STUDENT})

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

    C->>Svc: cu93ListUsedByStudent(user)
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

    Svc->>BPR: findAllByStudentAndStateAndDeletedAtIsNull(student, BenefitPurchaseState.USED)
    BPR-->>Svc: List<BenefitPurchase>
    alt Sin beneficios usados
        Svc->>BPM: toUsedDtoList([])
    else Beneficios usados encontrados
        Svc->>BPM: toUsedDtoList(benefitPurchases)
    end
    BPM-->>Svc: List<BenefitPurchasedUsedResponseDto>
    Svc-->>C: List<BenefitPurchasedUsedResponseDto>

    C->>SM: okSuccessfully()
    SM-->>C: "OK"
    C->>RF: ok(List<BenefitPurchasedUsedResponseDto>, "OK")
    RF->>RF: BaseResponse.builder()...
    RF-->>C: ResponseEntity<BaseResponse<List<BenefitPurchasedUsedResponseDto>>>
    C-->>ST: 200 OK<br/>BaseResponse{data: List<BenefitPurchasedUsedResponseDto>, message:"OK", errors:null, timestamp}

    note over Svc: finCU()

    classDef controller fill:#2196f3,stroke:#fff,color:#fff
    classDef service fill:#4caf50,stroke:#fff,color:#fff
    classDef repository fill:#ff9800,stroke:#fff,color:#fff
    classDef entity fill:#9e9e9e,stroke:#fff,color:#fff
    classDef mapper fill:#9c27b0,stroke:#fff,color:#fff

    class C controller
    class JA,JS,UES,UExS,SUAR,UGS,Svc,SGES,SM,RF service
    class UR,SR,BPR repository
    class User entity
    class BPM mapper
```


