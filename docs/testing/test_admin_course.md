# Reporte de pruebas — módulo admin/course

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 4.75 %
- Ramas: 4.01 %
- Líneas: 5.46 %
- Complejidad: 6.16 %
- Métodos: 7.43 %

### Módulo `admin/course`
- Instrucciones: 86.91 %
- Ramas: 88.89 %
- Líneas: 88.64 %
- Complejidad: 73.81 %
- Métodos: 72.73 %

## Escenarios (services/controllers/dtos)
Feature: CourseReadControllersTest
  Scenario: GET /admin/courses/{id} responde 200 con curso encontrado
  Scenario: GET /admin/courses/{id} responde 404 cuando el curso no existe
  Scenario: GET /admin/courses responde 200 con el listado activo
  Scenario: GET /admin/courses/paginated responde 200 con parámetros por defecto
  Scenario: GET /admin/courses/paginated responde 200 aplicando filtros y búsqueda

Feature: CourseWriteControllersTest
  Scenario: POST /admin/courses responde 201 con curso creado cuando los datos son válidos
  Scenario: POST /admin/courses responde 409 cuando el curso ya existe para el año
  Scenario: PUT /admin/courses/{id} responde 200 con curso actualizado
  Scenario: PUT /admin/courses/{id} responde 404 cuando el curso no existe
  Scenario: PUT /admin/courses/{id} responde 409 cuando el nombre ya existe en el año
  Scenario: DELETE /admin/courses/{id} responde 204 cuando elimina correctamente
  Scenario: DELETE /admin/courses/{id} responde 404 cuando el curso no existe
  Scenario: DELETE /admin/courses/{id} responde 409 cuando existen asociaciones

Feature: Course DTO validation
  Scenario: Course DTO validation
  Scenario: CourseRequestDto
  Scenario: Es válido cuando nombre y año cumplen restricciones
  Scenario: Produce violaciones cuando el nombre está vacío
  Scenario: Produce violaciones cuando el nombre supera 50 caracteres
  Scenario: Produce violaciones cuando el nombre contiene caracteres no permitidos
  Scenario: Produce violaciones cuando el año es nulo
  Scenario: CourseUpdateDto
  Scenario: Es válido cuando el nombre cumple restricciones
  Scenario: Produce violaciones cuando el nombre es nulo o vacío
  Scenario: Produce violaciones cuando el nombre supera 50 caracteres
  Scenario: Produce violaciones cuando el nombre contiene caracteres no permitidos

Feature: CourseDeleteService
  Scenario: CourseDeleteService
  Scenario: cu15DeleteCourse
  Scenario: Debe eliminar lógicamente el curso cuando no existen asociaciones
  Scenario: Debe lanzar ConflictException cuando existen estudiantes asociados
  Scenario: Debe lanzar ConflictException cuando existen materias asociadas
  Scenario: Debe propagar NotFoundException cuando el curso no existe o está eliminado

Feature: CourseGetService
  Scenario: CourseGetService
  Scenario: cu17GetCourse
  Scenario: Delegar en CourseGetByIdService y mapear a DTO

Feature: CourseListPaginatedService
  Scenario: CourseListPaginatedService
  Scenario: cu16ListPaginatedCourses
  Scenario: Retorna datos paginados sin búsqueda ni filtros adicionales
  Scenario: Aplica búsqueda textual y filtros dinámicos cuando están presentes
  Scenario: Ignora filtros cuando la lista de valores no coincide

Feature: CourseListService
  Scenario: CourseListService
  Scenario: cu9ListCourses
  Scenario: Retorna cursos activos en forma de DTO ordenados según repositorio

Feature: CourseRegisterService
  Scenario: CourseRegisterService
  Scenario: cu6RegisterCourse
  Scenario: Debe registrar un curso nuevo cuando el nombre no existe en el año
  Scenario: Debe lanzar ConflictException cuando el curso ya existe en el año
  Scenario: Debe propagar NotFoundException cuando el año no existe

Feature: CourseUpdateService
  Scenario: CourseUpdateService
  Scenario: cu14UpdateCourse
  Scenario: Debe actualizar el curso cuando no hay conflicto de nombres
  Scenario: Debe propagar NotFoundException si el curso no existe
  Scenario: Debe propagar ConflictException si el nombre ya existe en el mismo año

Feature: CourseCommonServices
  Scenario: CourseCommonServices
  Scenario: CourseExistByService
  Scenario: validate(String, Year) debe delegar en el repositorio
  Scenario: validate(Long) debe devolver el resultado del repositorio
  Scenario: validate(String) debe delegar en existsByName
  Scenario: validateExceptId debe lanzar ConflictException cuando ya existe el nombre en el mismo año
  Scenario: validateExceptId no debe lanzar excepciones cuando no existe duplicado
  Scenario: CourseGetByIdService
  Scenario: Debe devolver el curso cuando existe y no está eliminado
  Scenario: Debe lanzar NotFoundException cuando el curso no existe o está eliminado
  Scenario: CourseExistByYearService
  Scenario: Debe delegar en existsByYearIdAndDeletedAtIsNull
