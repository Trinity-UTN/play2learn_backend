# Diagrama de Secuencia: Listado paginado de plazos fijos

Este diagrama documenta el flujo completo del endpoint `GET /investment/fixed-term-deposit/paginated`, expuesto por `FixedTermDepositListPaginatedController` y que implementa el caso de uso `cu99ListPaginatedFixedTermDeposits`.

## Descripción General

1. Validación previa de sesión y roles mediante `@SessionRequired` y el interceptor `JwtSessionAspect`.
2. Verificación del token JWT: expiración, firma, usuario activo y rol permitido (`ROLE_STUDENT`).
3. Construcción de paginación (`PaginatorUtils.buildPageable`) y especificaciones dinámicas (`FixedTermDepositSpecs`) en el servicio.
4. Consulta paginada en `IFixedTermDepositRepository`, mapeo a DTOs con `FixedTermDepositMapper` y armado de metadatos con `PaginationHelper`.
5. Construcción de la respuesta `ResponseFactory.paginated`, usando el mensaje `SuccessfulMessages.okSuccessfully()`.

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor Est as Estudiante (ROLE_STUDENT)
    participant JA as JwtSessionAspect
    participant JS as IJwtService
    participant UEB as UserExistsByEmailService
    participant UES as UserExistsService
    participant UR as IUserRepository
    participant C as FixedTermDepositListPaginatedController
    participant S as FixedTermDepositListPaginatedService
    participant PU as PaginatorUtils
    participant FS as FixedTermDepositSpecs
    participant Repo as IFixedTermDepositRepository
    participant DB as Base de Datos
    participant FTD as FixedTermDeposit
    participant FM as FixedTermDepositMapper
    participant DTO as FixedTermDepositResponseDto
    participant PH as PaginationHelper
    participant RF as ResponseFactory
    participant SM as SuccessfulMessages

    Est->>C: GET /investment/fixed-term-deposit/paginated<br/>?page=1&page_size=10&order_by=id&order_type=asc<br/>&search=&filters=&filtersValues=<br/>Header: Authorization: Bearer {jwt}
    Note right of C: @SessionRequired(roles = {ROLE_STUDENT})<br/>@GetMapping("/paginated")

    Note over JA: Interceptor AOP ejecutado antes del método
    JA->>JA: validateJwt(joinPoint, sessionRequired)
    JA->>JA: attrs = RequestContextHolder.getRequestAttributes()
    alt attrs == null
        JA-->>Est: 500 Internal Server Error<br/>(NOT_HTTP)
    else solicitud HTTP válida
        JA->>JA: request = attrs.getRequest()
        JA->>JA: authHeader = request.getHeader("Authorization")
        alt authHeader inválido
            JA-->>Est: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
        else header válido
            JA->>JA: jwt = authHeader.substring(7)
            JA->>JS: isTokenExpired(jwt)
            alt token expirado
                JS-->>JA: JwtException
                JA-->>Est: 401 Unauthorized<br/>(TOKEN_EXPIRED)
            else token vigente
                JS-->>JA: boolean (false)
                JA->>JS: extractUsername(jwt)
                alt firma inválida
                    JS-->>JA: JwtException
                    JA-->>Est: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
                else firma válida
                    JS-->>JA: String email
                    JA->>UEB: validateIfUserIsActive(email)
                    UEB->>UES: validate(email)
                    UES->>UR: existsByEmailAndDeletedAtIsNull(email)
                    UR->>DB: SELECT EXISTS(... WHERE email = ? AND deleted_at IS NULL)
                    DB-->>UR: boolean
                    UR-->>UES: boolean
                    alt usuario inactivo/inexistente
                        UES-->>UEB: false
                        UEB-->>JA: UnauthorizedException(UNAUTHORIZED)
                        JA-->>Est: 401 Unauthorized
                    else usuario activo
                        UES-->>UEB: true
                        UEB-->>JA: validación exitosa
                        JA->>JS: extractRole(jwt)
                        JS-->>JA: String jwtRole
                        JA->>JA: requiredRoles = Arrays.asList(sessionRequired.roles())
                        alt rol no permitido
                            JA-->>Est: 401 Unauthorized<br/>(requiredRoles message)
                        else rol permitido
                            JA-->>C: autorización completada
                        end
                    end
                end
            end
        end
    end

    Note over C: Continúa la ejecución del método listPaginated(...)
    C->>C: listPaginated(page, pageSize, orderBy, orderType, search, filters, filtersValues)
    Note right of C: Parámetros con default:<br/>page=1, pageSize=10,<br/>orderBy="id", orderType="asc"<br/>search y filtros opcionales

    C->>S: cu99ListPaginatedFixedTermDeposits(page, pageSize, orderBy, orderType, search, filters, filtersValues)
    Note over S: El parámetro search se ignora en la implementación actual

    S->>PU: buildPageable(page, pageSize, orderBy, orderType)
    PU->>PU: direction = desc? DESC : ASC
    PU->>PU: sort = Sort.by(direction, orderBy)
    PU->>PU: PageRequest.of(Math.max(page-1, 0), pageSize, sort)
    PU-->>S: Pageable

    S->>S: spec = Specification.where(null)
    alt filtros válidos
        S->>S: verifica filters != null && filterValues != null && tamaños coinciden
        loop Por cada índice i de filters
            alt filters[i] == "fixedTermState"
                S->>FS: hasState(filterValues[i])
                alt valor inválido
                    FS-->>S: Specification (cb.conjunction())
                else valor válido
                    FS-->>S: Specification por estado
                end
            else filters[i] == "fixedTermDays"
                S->>FS: hasDays(filterValues[i])
                alt valor inválido
                    FS-->>S: Specification (cb.conjunction())
                else valor válido
                    FS-->>S: Specification por días
                end
            else otro campo
                S->>FS: genericFilter(filters[i], filterValues[i])
                alt campo inválido
                    FS-->>S: Specification (cb.conjunction())
                else campo válido
                    FS-->>S: Specification igualdad
                end
            end
            S->>S: spec = spec.and(specRetornada)
        end
    else sin filtros o listas inconsistentes
        Note right of S: Se mantiene Specification.where(null)
    end

    S->>Repo: findAll(spec, pageable)
    Repo->>DB: SELECT * FROM fixed_term_deposit<br/>WHERE spec AND filtros dinámicos<br/>ORDER BY {orderBy} {direction}<br/>LIMIT pageSize OFFSET (page-1)*pageSize
    DB-->>Repo: Page<FixedTermDeposit> pageResult
    Repo-->>S: Page<FixedTermDeposit>

    S->>S: contenido = pageResult.getContent()
    S-->>S: List<FixedTermDeposit> entidades

    S->>FM: toDtoList(entidades)
    loop Por cada FixedTermDeposit
        FM->>DTO: builder()<br/>.id(...).amountInvested(...).amountReward(...).fixedTermDays(...).startDate(...).endDate(...).fixedTermState(...).build()
        DTO-->>FM: FixedTermDepositResponseDto
    end
    FM-->>S: List<FixedTermDepositResponseDto> dtos

    S->>PH: fromPage(pageResult, dtos)
    PH->>PH: PaginatedData.builder()<br/>.results(dtos)<br/>.count((int) page.getTotalElements())<br/>.totalPages(page.getTotalPages())<br/>.currentPage(page.getNumber()+1)<br/>.pageSize(page.getSize())<br/>.build()
    PH-->>S: PaginatedData<FixedTermDepositResponseDto>

    S-->>C: PaginatedData<FixedTermDepositResponseDto>

    C->>SM: okSuccessfully()
    SM-->>C: "OK"

    C->>RF: paginated(data, "OK")
    RF->>RF: BaseResponse.builder()<br/>.data(data)<br/>.message("OK")<br/>.errors(null)<br/>.timestamp(nowUTC)<br/>.build()
    RF-->>C: ResponseEntity<BaseResponse<PaginatedData<FixedTermDepositResponseDto>>>

    C-->>Est: 200 OK<br/>BaseResponse{data: PaginatedData<FixedTermDepositResponseDto>, message: "OK", ...}

    note over S: finCU()

    class C controller
    class JA,JS,UEB,UES,PU,FS,PH,RF,SM service
    class Repo repository
    class FTD,DTO entity
    class FM mapper

    classDef controller fill:#2196f3,stroke:#fff,color:#fff
    classDef service fill:#4caf50,stroke:#fff,color:#fff
    classDef repository fill:#ff9800,stroke:#fff,color:#fff
    classDef entity fill:#9e9e9e,stroke:#fff,color:#fff
    classDef mapper fill:#9c27b0,stroke:#fff,color:#fff
```

## Parámetros de Entrada

- `page` (`int`, default `1`): número de página expuesto en base 1.
- `page_size` (`int`, default `10`): tamaño de página.
- `order_by` (`String`, default `"id"`): campo utilizado para ordenar.
- `order_type` (`String`, default `"asc"`): tipo de orden (`"asc"` o `"desc"`).
- `search` (`String`, opcional): parámetro actualmente **no utilizado** por el servicio.
- `filters` (`List<String>`, opcional): nombres de campos a filtrar (`fixedTermState`, `fixedTermDays` u otros).
- `filtersValues` (`List<String>`, opcional): valores correspondientes a cada filtro; deben tener el mismo tamaño que `filters`.

## Respuesta

El controlador retorna un `ResponseEntity<BaseResponse<PaginatedData<FixedTermDepositResponseDto>>>`:

- `data`: contiene `results` (lista paginada de `FixedTermDepositResponseDto`), `count`, `totalPages`, `currentPage` (convertido a base 1) y `pageSize`.
- `message`: `"OK"`.
- `errors`: `null`.
- `timestamp`: instante actual en formato ISO 8601 (UTC).

## Notas Relevantes

- Si se proveen filtros inválidos o valores que no coinciden con los enums (`FixedTermState`, `FixedTermDays`), `FixedTermDepositSpecs` retorna `cb.conjunction()` para ignorarlos sin fallar.
- El repositorio utiliza `JpaSpecificationExecutor` para combinar las especificaciones dinámicas con la paginación de Spring Data.
- El flujo depende del interceptor `JwtSessionAspect`; cualquier error previo en la validación del token impide que el controlador procese la solicitud.
- Este diagrama debe mantenerse sincronizado ante cambios en la lógica del servicio o en los filtros admitidos.

