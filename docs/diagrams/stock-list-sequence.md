# Diagrama de Secuencia: Listado de Acciones

Este diagrama muestra el flujo completo del endpoint `GET /investment/stocks`, desde el controlador `StockListController` hasta todas las capas involucradas en la obtención de la lista de acciones.

## Descripción del Flujo

El caso de uso `cu86listStocks` permite a usuarios con roles `ROLE_DEV`, `ROLE_ADMIN`, `ROLE_STUDENT` o `ROLE_TEACHER` obtener la lista completa de acciones disponibles en el sistema. El flujo incluye:

1. Validación de la sesión y permisos mediante `@SessionRequired` y el interceptor `JwtSessionAspect`
2. Validación del JWT token (expiración, firma, usuario activo)
3. Validación del rol del usuario contra los roles permitidos
4. Obtención de todas las acciones mediante `IStockRepository.findAll()`
5. Conversión de las entidades `Stock` a DTOs mediante `StockMapper`
6. Retorno de la respuesta HTTP 200 OK con la lista de acciones

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor U as User (DEV/ADMIN/STUDENT/TEACHER)
    participant JA as JwtSessionAspect
    participant C as StockListController
    participant Svc as StockListService
    participant SR as IStockRepository
    participant Stock as Stock
    participant SM as StockMapper
    participant DTO as StockResponseDto
    participant Resp as ResponseFactory
    participant JS as IJwtService
    participant UES as UserExistsByEmailService
    participant UExS as UserExistService
    participant UR as IUserRepository
    participant User as User

    U->>C: GET /investment/stocks<br/>Header: Authorization: Bearer {jwt}
    Note right of C: @SessionRequired(roles = {<br/>ROLE_DEV, ROLE_ADMIN,<br/>ROLE_STUDENT, ROLE_TEACHER})<br/>@GetMapping
    
    Note over JA: Interceptor ejecutado antes del método
    JA->>JA: validateJwt(joinPoint, sessionRequired)
    Note right of JA: Obtiene la solicitud HTTP actual
    JA->>JA: RequestContextHolder.getRequestAttributes()
    JA->>JA: request.getHeader("Authorization")
    
    alt Authorization header no existe o no es Bearer
        JA-->>U: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    end
    
    JA->>JA: jwt = authHeader.substring(7)
    Note right of JA: Extrae el JWT del header
    
    JA->>JS: isTokenExpired(jwt: String)
    Note right of JS: Valida que el token<br/>no haya expirado
    JS->>JS: extractExpiration(jwt)
    JS->>JS: Verifica si expiration < now
    alt Token expirado
        JS-->>JA: JwtException
        JA-->>U: 401 Unauthorized<br/>(TOKEN_EXPIRED)
    else Token válido
        JS-->>JA: boolean (false = no expirado)
    end
    
    JA->>JS: extractUsername(jwt: String)
    Note right of JS: Extrae el username<br/>del token y valida la firma
    JS->>JS: extractAllClaims(jwt)<br/>parseClaimsJws(token)
    JS->>JS: extractClaim(token, Claims::getSubject)
    alt Firma inválida
        JS-->>JA: JwtException
        JA-->>U: 401 Unauthorized<br/>(INVALID_ACCESS_TOKEN)
    else Firma válida
        JS-->>JA: String (email/username)
    end
    
    JA->>UES: validateIfUserIsActive(email: String)
    Note right of UES: Valida que el usuario<br/>exista y esté activo
    UES->>UExS: validate(email: String)
    UExS->>UR: existsByEmailAndDeletedAtIsNull(email: String)
    Note right of UR: Busca en la base de datos<br/>si el usuario existe<br/>y no está eliminado
    UR->>User: query by email AND deleted_at IS NULL
    User-->>UR: boolean
    UR-->>UExS: boolean
    alt Usuario no existe o está eliminado
        UExS-->>UES: boolean (false)
        UES-->>JA: UnauthorizedException<br/>(UNAUTHORIZED)
        JA-->>U: 401 Unauthorized
    else Usuario existe y está activo
        UExS-->>UES: boolean (true)
        UES-->>JA: void (validación exitosa)
    end
    
    JA->>JS: extractRole(jwt: String)
    Note right of JS: Extrae el rol del token
    JS->>JS: extractAllClaims(jwt)<br/>get("role", String.class)
    JS-->>JA: String (role)
    
    JA->>JA: Arrays.asList(sessionRequired.roles())
    JA->>JA: requiredRoles.contains(jwtRole)
    Note right of JA: Valida que el rol del token<br/>esté en los roles permitidos
    alt Rol no permitido
        JA-->>U: 401 Unauthorized<br/>(requiredRoles message)
    else Rol permitido
        JA->>JA: Validación exitosa
    end
    
    Note over JA,C: Si todas las validaciones pasan,<br/>el método del controlador se ejecuta
    C->>Svc: cu86listStocks()
    Note right of Svc: Obtiene todas las acciones<br/>y las convierte a DTOs
    
    Svc->>SR: findAll()
    Note right of SR: Consulta todas las acciones<br/>en la base de datos
    SR->>Stock: SELECT * FROM stocks
    Stock-->>SR: List<Stock>
    SR-->>Svc: List<Stock>
    
    Svc->>SM: toDtoList(stocks: List<Stock>)
    Note right of SM: Convierte lista de entidades<br/>a lista de DTOs
    loop Para cada Stock
        SM->>SM: toDto(stock: Stock, null, null)
        Note right of SM: Mapea cada entidad a DTO:<br/>- id: stock.getId()<br/>- name: stock.getName()<br/>- abbreviation: stock.getAbbreviation()<br/>- totalAmount: stock.getTotalAmount()<br/>- availableAmount: stock.getAvailableAmount()<br/>- soldAmount: stock.getSoldAmount()<br/>- currentPrice: stock.getCurrentPrice()<br/>- initialPrice: stock.getInitialPrice()<br/>- riskLevel: stock.getRiskLevel()<br/>- quantityBought: BigInteger.ZERO<br/>- pendingOrders: null
        SM->>DTO: builder()<br/>.id()<br/>.name()<br/>.abbreviation()<br/>.totalAmount()<br/>.availableAmount()<br/>.soldAmount()<br/>.currentPrice()<br/>.initialPrice()<br/>.riskLevel()<br/>.quantityBought(BigInteger.ZERO)<br/>.pendingOrders(null)<br/>.build()
        DTO-->>SM: StockResponseDto
    end
    SM-->>Svc: List<StockResponseDto>
    
    Svc-->>C: List<StockResponseDto>
    
    C->>Resp: ok(data: List<StockResponseDto>,<br/>message: SuccessfulMessages.okSuccessfully())
    Note right of Resp: Crea ResponseEntity con:<br/>- status: HttpStatus.OK (200)<br/>- body: BaseResponse<List<StockResponseDto>><br/>- message: "Operación realizada exitosamente"<br/>- timestamp: ISO_INSTANT format
    Resp->>Resp: getCurrentTimestamp()<br/>ISO_INSTANT format
    Resp->>Resp: BaseResponse.builder()<br/>.data(data)<br/>.message(message)<br/>.errors(null)<br/>.timestamp(timestamp)<br/>.build()
    Resp->>Resp: ResponseEntity.ok(baseResponse)
    Resp-->>C: ResponseEntity<BaseResponse<List<StockResponseDto>>>
    
    C-->>U: 200 OK<br/>BaseResponse<List<StockResponseDto>>

    note over Svc: finCU()
```

## Componentes Involucrados

### Controller
- **StockListController**: Punto de entrada del endpoint REST. Valida la sesión y permisos mediante `@SessionRequired`, recibe la petición GET y retorna la respuesta HTTP con la lista de acciones.

### Interceptor
- **JwtSessionAspect**: Interceptor de Spring AOP que se ejecuta antes de los métodos anotados con `@SessionRequired`. Valida el JWT token, la expiración, la firma, el usuario activo y los roles permitidos.

### Services
- **StockListService**: Contiene la lógica de negocio del caso de uso. Obtiene todas las acciones del repositorio y las convierte a DTOs mediante el mapper.
- **UserExistsByEmailService**: Servicio que valida si un usuario existe y está activo en el sistema. Lanza `UnauthorizedException` si el usuario no existe o está eliminado.
- **UserExistService**: Servicio común que valida la existencia de un usuario mediante su email. Retorna `true` si el usuario existe y no está eliminado.

### Mappers
- **StockMapper**: Responsable de convertir entre entidades `Stock` y DTOs `StockResponseDto`. Incluye métodos para convertir entidades individuales y listas. En este caso, usa `toDtoList` que mapea cada `Stock` a `StockResponseDto` con `quantityBought = BigInteger.ZERO` y `pendingOrders = null`.

### Repositories
- **IStockRepository**: Interfaz de persistencia para la entidad `Stock`. Extiende `CrudRepository<Stock, Long>`. Proporciona el método `findAll()` para obtener todas las acciones.
- **IUserRepository**: Interfaz de persistencia para la entidad `User`. Proporciona el método `existsByEmailAndDeletedAtIsNull` para validar si un usuario existe y está activo.

### JWT Service
- **IJwtService**: Servicio que maneja la generación, validación y extracción de información de tokens JWT. Proporciona métodos para:
  - `isTokenExpired`: Valida si un token ha expirado
  - `extractUsername`: Extrae el username del token y valida la firma
  - `extractRole`: Extrae el rol del token

### Entities
- **Stock**: Entidad que representa una acción en el sistema. Contiene información como nombre, abreviación, precios (inicial y actual), cantidades (total, disponible, vendida) y nivel de riesgo.
- **User**: Entidad que representa un usuario en el sistema. Contiene información como email, rol, y fecha de eliminación (soft delete).

### DTOs
- **StockResponseDto**: DTO de respuesta que contiene la información de una acción. Incluye id, name, abbreviation, totalAmount, availableAmount, soldAmount, currentPrice, initialPrice, riskLevel, quantityBought y pendingOrders.

### Response Factory
- **ResponseFactory**: Utilidad para crear respuestas HTTP estandarizadas. Proporciona el método `ok` que crea una respuesta HTTP 200 OK con un `BaseResponse` que incluye los datos, mensaje y timestamp.

### Messages
- **SuccessfulMessages**: Clase utilitaria que proporciona mensajes de éxito estandarizados. El método `okSuccessfully()` retorna el mensaje "Operación realizada exitosamente".

## Validaciones

- **Nivel de Interceptor (JwtSessionAspect)**: 
  - Validación de existencia del header `Authorization` y formato `Bearer {token}`
  - Validación de expiración del token JWT
  - Validación de la firma del token JWT
  - Validación de existencia y estado activo del usuario
  - Validación del rol del usuario contra los roles permitidos (`ROLE_DEV`, `ROLE_ADMIN`, `ROLE_STUDENT`, `ROLE_TEACHER`)

- **Nivel de Repository**: 
  - La consulta `findAll()` obtiene todas las acciones sin filtros. Si no hay acciones, retorna una lista vacía.

## Manejo de Errores

- **401 Unauthorized**: Se retorna si:
  - El header `Authorization` no existe o no tiene el formato correcto (`INVALID_ACCESS_TOKEN`)
  - El token JWT ha expirado (`TOKEN_EXPIRED`)
  - La firma del token JWT es inválida (`INVALID_ACCESS_TOKEN`)
  - El usuario no existe o está eliminado (`UNAUTHORIZED`)
  - El rol del usuario no está en los roles permitidos (mensaje con los roles requeridos)

- **500 Internal Server Error**: Se retorna si la solicitud no es HTTP (`NOT_HTTP`), aunque esto es muy poco probable en un entorno normal.

## Notas Adicionales

- El endpoint no requiere parámetros. Retorna todas las acciones disponibles en el sistema.
- La lista puede estar vacía si no hay acciones registradas en el sistema.
- Los campos `quantityBought` y `pendingOrders` en el `StockResponseDto` siempre son `BigInteger.ZERO` y `null` respectivamente, ya que este endpoint no incluye información específica del usuario que realiza la petición.
- El interceptor `JwtSessionAspect` se ejecuta automáticamente antes del método del controlador gracias a la anotación `@Before("@annotation(sessionRequired)")` de Spring AOP.
- La validación del token JWT incluye la validación de la firma mediante `parseClaimsJws`, que lanza una excepción si la firma no es válida.
- El timestamp en el `BaseResponse` se genera en formato ISO_INSTANT (UTC) al momento de crear la respuesta.

