# Estrategia de pruebas unitarias — Módulo Admin Year

## 1. Objetivos
- Garantizar que cada servicio del módulo `admin/year` aplica correctamente las reglas de negocio y lanza las excepciones esperadas.
- Verificar la consistencia de mappers, DTOs y modelo (`Year`) con énfasis en el borrado lógico.
- Asegurar que la capa de especificaciones (`YearSpecs`) y utilidades de paginación funcionan con combinaciones de filtros y búsquedas.
- Alcanzar al menos **85 % de cobertura de líneas** y **80 % de ramas** para la carpeta `admin/year`.

## 2. Alcance
- Servicios: `YearRegisterService`, `YearUpdateService`, `YearDeleteService`, `YearGetService`, `YearListService`, `YearListPaginatedService`, `YearExistService`, `YearGetByIdService`.
- Mappers y modelos: `YearMapper`, `Year`.
- Especificaciones: `YearSpecs`.
- Exclusiones: capas de controlador y anotaciones `@SessionRequired` (cubiertas en pruebas de integración/end-to-end).

## 3. Riesgos y mitigaciones
- **Errores en mensajes de excepción:** comparar mensajes generados por las factorías (`ConflictExceptionMessages`, etc.) en lugar de cadenas literales.
  - *Mitigación:* centralizar constantes esperadas en helpers de pruebas.
- **Flakiness por fechas (`deletedAt`):** riesgo mínimo.
  - *Mitigación:* usar `Clock` controlado o tolerancia al comparar `LocalDateTime`.
- **Dependencia excesiva de mocks:** servicios que combinan specs y paginación pueden volverse frágiles.
  - *Mitigación:* utilizar `ArgumentCaptor` y `Specification` combinadas verificando interacción mínima.

## 4. Métricas de calidad
- Cobertura JaCoCo ≥ 85 % líneas y ≥ 80 % ramas.
- Mutación (si se ejecuta posteriormente) sin mutantes vivos en reglas críticas: duplicidad de nombre, borrado lógico, filtros `YearSpecs`.
- Tiempo de ejecución objetivo < 5 s para la suite del módulo.

## 5. Datos de prueba y fixtures
- **DTOs válidos:** nombres como `"Primero Básico"`; caracteres acentuados permitidos.
- **DTOs inválidos:** cadenas vacías, >50 caracteres o con símbolos especiales.
- **Entidad base `Year`:** usar builder `Year.builder().id(1L).name("Primero").deletedAt(null).build()`.
- **Paginación:** listas de objetos `Year` simuladas (`Arrays.asList(...)`) y mocks de `Page<Year>`.
- **Fechas:** `LocalDateTime.now()` capturado al inicio del test o `Clock.fixed` en tests de modelo.

## 6. Dobles de prueba
- **Mocks (Mockito):** `IYearRepository`, `IYearRepositoryPaginated`, `IYearExistService`, `IYearGetByIdService`, `ICourseExistByYearService`, `PaginatorUtils`, `PaginationHelper`.
- **Captor:** para capturar `Year` al guardar en repositorio y verificar cambios de estado.
- **Stub simple:** `Specification<Year>` generada por `YearSpecs`; se validará por combinación, no se mockea la clase estática.

## 7. Matriz de casos de prueba

### Servicios
| Clase | Casos clave |
|-------|-------------|
| `YearRegisterService` | Registro exitoso; conflicto por nombre existente |
| `YearUpdateService` | Actualización exitosa; `validateExceptId` lanza conflicto; `findById` lanza NotFound |
| `YearDeleteService` | ID inválido (`BadRequestException`); año ya eliminado; año con cursos asociados; eliminación exitosa cambia `deletedAt` |
| `YearGetService` | Delegación a `YearGetByIdService`; mapeo correcto |
| `YearListService` | Filtrado por `deletedAt` nulo; conversión a lista DTO |
| `YearListPaginatedService` | Construcción de `Pageable`; aplicación de `notDeleted`; combinación con `search`; aplicación de filtros múltiples; retorno `PaginatedData` |
| `YearExistService` | `validate` true/false; `validate(Long)` true/false; `validateExceptId` lanza conflicto |
| `YearGetByIdService` | Retorno exitoso; `NotFoundException` con mensaje correcto |

### Mappers y modelo
| Clase | Casos clave |
|-------|-------------|
| `YearMapper` | `toModel` mapea correctamente; `toDto`; `toListDto` itera Iterable |
| `Year` | `delete()` establece `deletedAt`; `restore()` limpia `deletedAt` |

### Especificaciones
| Método | Casos clave |
|--------|-------------|
| `notDeleted` | Genera predicado `deletedAt IS NULL` |
| `nameContains` | Transformación a lower-case y `LIKE %search%` |
| `genericFilter` | Campo válido produce igualdad; campo inválido retorna conjunción |

## 8. Organización de archivos de prueba
- Directorio base: `src/test/java/trinity/play2learn/backend/admin/year/`.
- Subcarpetas:
  - `register/YearRegisterServiceTest.java`
  - `update/YearUpdateServiceTest.java`
  - `delete/YearDeleteServiceTest.java`
  - `find/` para `YearListServiceTest`, `YearListPaginatedServiceTest`, `YearGetServiceTest`, `YearExistServiceTest`, `YearGetByIdServiceTest`, `YearSpecsTest`, `YearMapperTest`
  - `models/YearModelTest.java`
- Reutilizar helpers en clases utilitarias (`YearTestDataFactory`) si se detecta duplicación significativa en T04/T08/T10/T12.

## 9. Flujo de trabajo recomendado
1. Implementar suites según T03–T12 siguiendo orden de prioridad (servicios principales > auxiliares > specs > mappers/modelo).
2. Ejecutar `mvn test` localmente tras cada bloque de generación/refinamiento.
3. Al finalizar las suites, correr `mvn verify -Psecurity` (T13) y `mvn test` final con reporte JaCoCo (T14).
4. Documentar resultados en esta estrategia (sección 10).

## 10. Registro de resultados (a completar)
- `mvn test`: _pendiente_
- Cobertura JaCoCo: _pendiente_
- Hallazgos relevantes: _pendiente_
- Acciones de seguimiento: _pendiente_

