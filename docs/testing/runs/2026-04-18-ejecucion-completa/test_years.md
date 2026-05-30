# Reporte de pruebas — módulo admin/year

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 54.62 %
- Ramas: 49.07 %
- Líneas: 53.21 %
- Complejidad: 42.99 %
- Métodos: 44.63 %

### Módulo `admin/year`
- Instrucciones: 73.95 %
- Ramas: 85.0 %
- Líneas: 81.48 %
- Complejidad: 59.26 %
- Métodos: 52.94 %

## Escenarios (services/controllers/dtos)
Feature: YearDeleteService
  Scenario: YearDeleteService
  Scenario: cu11deleteYear
  Scenario: Rechaza IDs no numéricos
  Scenario: Rechaza años ya eliminados lógicamente
  Scenario: Impide eliminar años con cursos asociados
  Scenario: Marca como eliminado y persiste cuando no hay restricciones

Feature: YearExistService
  Scenario: YearExistService
  Scenario: validate(name)
  Scenario: Devuelve true cuando el nombre ya existe (comparación case-insensitive)
  Scenario: Devuelve false cuando el nombre no existe
  Scenario: validate(id)
  Scenario: Devuelve true cuando existe un año con el ID indicado
  Scenario: Devuelve false cuando no existe un año con el ID indicado
  Scenario: validateExceptId
  Scenario: No lanza excepción cuando el nombre solo coincide con el mismo ID
  Scenario: Lanza ConflictException cuando el nombre ya existe en otro año

Feature: YearGetService
  Scenario: YearGetService
  Scenario: cu13GetYear
  Scenario: Obtiene un año por ID y lo transforma a DTO

Feature: YearListPaginatedService
  Scenario: YearListPaginatedService
  Scenario: cu12PaginatedListYears
  Scenario: Retorna página sin filtros adicionales y mapea contenido a DTO
  Scenario: Aplica búsqueda textual y filtros dinámicos antes de consultar el repositorio

Feature: YearListService
  Scenario: YearListService
  Scenario: cu8ListYears
  Scenario: Retorna la lista de años activos transformada a DTO

Feature: YearRegisterService
  Scenario: YearRegisterService
  Scenario: cu7RegisterYear
  Scenario: Debe registrar un año cuando el nombre no existe
  Scenario: Debe lanzar ConflictException cuando el nombre ya existe

Feature: YearUpdateService
  Scenario: YearUpdateService
  Scenario: cu10UpdateYear
  Scenario: Debe actualizar el nombre del año cuando el id existe y el nombre es único
  Scenario: Debe lanzar ConflictException cuando el nuevo nombre ya está en uso en otro año
  Scenario: Debe lanzar NotFoundException cuando el año a actualizar no existe
