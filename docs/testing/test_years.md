Feature: Gestión de años académicos - Cobertura unitaria
  Contexto:
    Dado que el módulo `admin/year` expone servicios, mappers y utilidades para manipular años académicos
    Y que se dispone de suites unitarias aisladas con Mockito y AssertJ

  # YearDeleteServiceTest ----------------------------------------------------
  Escenario: Rechazo de identificadores no numéricos al eliminar un año
    Dado que existe el servicio `YearDeleteService`
    Y que el identificador recibido es "abc"
    Cuando se ejecuta `cu11deleteYear`
    Entonces se lanza `BadRequestException` con el mensaje de formato inválido
    Y no se invoca a los repositorios ni a servicios adicionales

  Escenario: Impedir eliminación cuando el año ya está marcado como borrado
    Dado un año con `deletedAt` no nulo recuperado por `YearGetByIdService`
    Cuando se invoca `cu11deleteYear`
    Entonces se lanza `BadRequestException` indicando que el recurso ya fue eliminado
    Y no se ejecuta `yearRepository.save`

  Escenario: Evitar eliminación si existen cursos asociados
    Dado un año activo asociado a cursos según `ICourseExistByYearService`
    Cuando se invoca `cu11deleteYear`
    Entonces se lanza `ConflictException` señalando asociaciones con cursos
    Y no se persiste ningún cambio en el repositorio

  Escenario: Eliminar lógicamente un año sin restricciones
    Dado un año activo sin asociaciones
    Cuando se ejecuta `cu11deleteYear`
    Entonces el año queda con `deletedAt` no nulo
    Y se persiste el cambio mediante `yearRepository.save`

  # YearRegisterServiceTest --------------------------------------------------
  Escenario: Registrar un año cuando el nombre es único
    Dado que `IYearExistService` devuelve falso para el nombre solicitado
    Cuando se invoca `cu7RegisterYear`
    Entonces se guarda el nuevo año
    Y la respuesta incluye el id generado y el nombre enviado

  Escenario: Impedir registro si el nombre ya existe
    Dado que `IYearExistService` indica que el nombre está duplicado
    Cuando se ejecuta `cu7RegisterYear`
    Entonces se lanza `ConflictException` con el mensaje de recurso duplicado
    Y no se guarda ningún registro

  # YearUpdateServiceTest ----------------------------------------------------
  Escenario: Actualizar el nombre de un año existente
    Dado un año recuperado por `YearGetByIdService`
    Y que `validateExceptId` no detecta duplicados
    Cuando se ejecuta `cu10UpdateYear`
    Entonces se actualiza el nombre y se persiste con `yearRepository.save`

  Escenario: Evitar actualización si el nuevo nombre está en uso
    Dado un año existente distinto con el mismo nombre
    Cuando se invoca `cu10UpdateYear`
    Entonces se lanza `ConflictException`
    Y no se ejecuta la persistencia

  Escenario: Notificar ausencia de año al actualizar
    Dado que `YearGetByIdService` lanza `NotFoundException`
    Cuando se ejecuta `cu10UpdateYear`
    Entonces se propaga la excepción y no se interactúa con el repositorio

  # YearListServiceTest ------------------------------------------------------
  Escenario: Listar años activos
    Dado que el repositorio retorna años con `deletedAt` en null
    Cuando se ejecuta `cu8ListYears`
    Entonces se transforma la colección en DTOs manteniendo el orden

  # YearListPaginatedServiceTest ---------------------------------------------
  Escenario: Listado paginado sin filtros adicionales
    Dado un `Page` con años activos
    Y paginación construida vía `PaginatorUtils`
    Cuando se invoca `cu12PaginatedListYears` sin filtros ni búsqueda
    Entonces se retorna un `PaginatedData` con los DTOs resultantes

  Escenario: Listado paginado aplicando búsqueda y filtros dinámicos
    Dado un `Page` vacío y un término de búsqueda "Básico"
    Cuando se ejecuta `cu12PaginatedListYears` con filtros dinámicos
    Entonces se consulta el repositorio con la especificación combinada
    Y se obtiene un `PaginatedData` con lista vacía

  # YearGetServiceTest -------------------------------------------------------
  Escenario: Obtener un año por id y transformarlo a DTO
    Dado un año recuperado por `YearGetByIdService`
    Cuando se llama a `cu13GetYear`
    Entonces se devuelve un DTO con el id y nombre esperados

  # YearExistServiceTest -----------------------------------------------------
  Escenario: Validar existencia de nombre duplicado
    Dado que el repositorio confirma la existencia del nombre
    Cuando se ejecuta `validate(String)`
    Entonces el resultado es verdadero

  Escenario: Validar inexistencia de nombre
    Dado que el repositorio indica que el nombre no existe
    Cuando se ejecuta `validate(String)`
    Entonces el resultado es falso

  Escenario: Validar existencia de id
    Dado que `existsById` responde verdadero
    Cuando se ejecuta `validate(Long)`
    Entonces el resultado es verdadero

  Escenario: Validar excepción por nombre duplicado en otro registro
    Dado que `existsByNameIgnoreCaseAndIdNot` devuelve verdadero
    Cuando se ejecuta `validateExceptId`
    Entonces se lanza `ConflictException` con el mensaje adecuado

  # YearGetByIdServiceTest ---------------------------------------------------
  Escenario: Recuperar año existente por id
    Dado un `Optional` con la entidad encontrada
    Cuando se ejecuta `findById`
    Entonces se obtiene la entidad original

  Escenario: Notificar ausencia de año por id
    Dado un `Optional.empty()` en el repositorio
    Cuando se ejecuta `findById`
    Entonces se lanza `NotFoundException` con el mensaje parametrizado

  # YearMapperTest -----------------------------------------------------------
  Escenario: Construir modelo a partir de YearRequestDto
    Dado un DTO con el nombre del año
    Cuando se invoca `toModel`
    Entonces se obtiene un `Year` sin id y con el nombre copiado

  Escenario: Construir YearResponseDto a partir del modelo
    Dado un `Year` con id y nombre
    Cuando se ejecuta `toDto`
    Entonces se retorna un DTO con los mismos valores

  Escenario: Transformar un Iterable de años a lista de DTOs
    Dado una lista de años activos
    Cuando se llama a `toListDto`
    Entonces se obtiene una lista de DTOs en el mismo orden

  # YearModelTest ------------------------------------------------------------
  Escenario: Marcar un año como eliminado
    Dado un `Year` activo
    Cuando se invoca `delete`
    Entonces `deletedAt` se establece con una marca temporal reciente

  Escenario: Restaurar un año eliminado
    Dado un `Year` con `deletedAt` poblado
    Cuando se ejecuta `restore`
    Entonces `deletedAt` vuelve a ser nulo

  # YearSpecsTest ------------------------------------------------------------
  Escenario: Generar predicado para años no eliminados
    Dado el `CriteriaBuilder` y el root de `Year`
    Cuando se invoca `notDeleted`
    Entonces se produce `deletedAt IS NULL`

  Escenario: Generar predicado LIKE case-insensitive
    Dado el término de búsqueda "BÁsico"
    Cuando se ejecuta `nameContains`
    Entonces se construye un `LIKE '%básico%'`

  Escenario: Generar predicado equal dinámico válido
    Dado el campo "name" y el valor "Primero Básico"
    Cuando se ejecuta `genericFilter`
    Entonces se produce un `=` sobre dicho campo

  Escenario: Ignorar filtros con campos inexistentes
    Dado un nombre de campo inválido
    Cuando se ejecuta `genericFilter`
    Entonces se devuelve una conjunción (sin efecto)

## Cobertura agregada (JaCoCo — 2025-11-11)

- Proyecto completo (`target/site/jacoco/jacoco.csv`)
  - Instrucciones: 1.98 %
  - Ramas: 2.18 %
  - Líneas: 2.07 %
  - Complejidad: 2.65 %
  - Métodos: 3.04 %

- Módulo `admin/year`
  - Instrucciones: 78.80 %
  - Ramas: 86.36 %
  - Líneas: 84.62 %
  - Complejidad: 68.29 %
  - Métodos: 66.67 %

