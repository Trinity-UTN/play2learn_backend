# Reporte de pruebas — módulo admin/student

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 13.51 %
- Ramas: 10.91 %
- Líneas: 13.69 %
- Complejidad: 13.76 %
- Métodos: 16.13 %

### Módulo `admin/student`
- Instrucciones: 90.31 %
- Ramas: 85.71 %
- Líneas: 94.05 %
- Complejidad: 75.86 %
- Métodos: 77.27 %

## Escenarios (services/controllers/dtos)
Feature: GET /admin/students/{id}
  Scenario: GET /admin/students/{id}
  Scenario: When student exists Then returns 200 with payload
  Scenario: When student not found Then returns 404
  Scenario: GET /admin/students
  Scenario: When students exist Then returns 200 with list
  Scenario: When there are no students Then returns empty list
  Scenario: GET /admin/students/paginated
  Scenario: When paginated retrieval succeeds Then returns 200 with metadata
  Scenario: When paginated retrieval returns empty Then returns 200 with empty results
  Scenario: When underlying service throws NotFound Then returns 404

Feature: POST /admin/students
  Scenario: POST /admin/students
  Scenario: When registration succeeds Then returns 201 with response data
  Scenario: When registration conflicts Then returns 409
  Scenario: PUT /admin/students/{id}
  Scenario: When update succeeds Then returns 201 with updated payload
  Scenario: When student not found Then returns 404
  Scenario: DELETE /admin/students/{id}
  Scenario: When deleting existing student Then returns 204
  Scenario: When student not found on delete Then returns 404
  Scenario: PATCH /admin/students/restore/{id}
  Scenario: When restore succeeds Then returns 200 with DTO
  Scenario: When restore student missing Then returns 404

Feature: StudentRequestDto validation
  Scenario: StudentRequestDto validation
  Scenario: When request is valid Then no violations are returned
  Scenario: When name is null Then reports NOT_EMPTY_NAME
  Scenario: When lastname contains invalid characters Then reports PATTERN_LASTNAME
  Scenario: When email is invalid Then reports PATTERN_EMAIL
  Scenario: When DNI has less digits Then reports PATTERN_DNI
  Scenario: When course id is null Then reports NOT_NULL_COURSE
  Scenario: When tutor email is invalid Then reports PATTERN_EMAIL
  Scenario: StudentUpdateRequestDto validation
  Scenario: When update dto is valid Then no violations are returned
  Scenario: When name is null Then reports NOT_EMPTY_NAME
  Scenario: When lastname pattern fails Then reports PATTERN_LASTNAME
  Scenario: When DNI has non numeric characters Then reports PATTERN_DNI
  Scenario: When course id is null Then reports NOT_NULL_COURSE
  Scenario: When tutor email is invalid Then reports PATTERN_EMAIL

Feature: ✅ Registro exitoso
  Scenario: ✅ Registro exitoso
  Scenario: Debería registrar un estudiante correctamente con todos los datos anidados
  Scenario: ❌ Validaciones de email
  Scenario: No debe permitir registrar un estudiante con un email inválido
  Scenario: No debe permitir registrar un estudiante con email vacío
  Scenario: No debe permitir registrar un estudiante con email que supere 100 caracteres
  Scenario: ❌ Validaciones de DNI
  Scenario: No debe permitir registrar un estudiante con puntos en el DNI
  Scenario: No debe permitir registrar un estudiante con DNI vacío
  Scenario: No debe permitir registrar un estudiante con DNI que supere 8 dígitos
  Scenario: No debe permitir registrar un estudiante con DNI que tenga menos de 8 dígitos
  Scenario: Name Validation Tests
  Scenario: No debe permitir name con caracteres no alfabéticos
  Scenario: Debería permitir name con 49 caracteres (límite válido)
  Scenario: No debe permitir name con 51 caracteres (límite excedido)
  Scenario: No debe permitir name vacío
  Scenario: Lastame Validation Tests
  Scenario: No debe permitir lastname con caracteres no alfabéticos
  Scenario: Debería permitir name con 49 caracteres (límite válido)
  Scenario: No debe permitir name con 51 caracteres (límite excedido)
  Scenario: No debe permitir name vacío

Feature: cu19DeleteStudent
  Scenario: cu19DeleteStudent
  Scenario: Given existing student When deleting Then marks as deleted and persists
  Scenario: Given student not found When deleting Then propagates NotFoundException

Feature: cu22GetStudent
  Scenario: cu22GetStudent
  Scenario: Given existing student When retrieving Then returns mapped DTO
  Scenario: Given student not found When retrieving Then propagates NotFoundException

Feature: cu21ListPaginatedStudents
  Scenario: cu21ListPaginatedStudents
  Scenario: Given search and filters When listing Then applies specifications and returns paginated data
  Scenario: Given blank search and mismatched filters When listing Then skips specifications and returns empty results

Feature: cu4registerStudent
  Scenario: cu4registerStudent
  Scenario: Given valid student request When registering Then creates user, persists student with profile/wallet and assigns subjects
  Scenario: Given course is missing When registering student Then propagates NotFoundException and stops flow
  Scenario: Given duplicate user detected When registering student Then throws ConflictException and avoids persistence

Feature: cu38RestoreStudent
  Scenario: cu38RestoreStudent
  Scenario: Given deleted student When restoring Then removes deletedAt and returns DTO
  Scenario: Given deleted student not found When restoring Then propagates NotFoundException

Feature: cu20ListStudents
  Scenario: cu20ListStudents
  Scenario: Given students exist When listing Then returns mapped DTOs
  Scenario: Given no students When listing Then returns empty list

Feature: cu18updateStudent
  Scenario: cu18updateStudent
  Scenario: Given valid update request with new course When updating Then persists student with updated data
  Scenario: Given duplicated DNI in update request When updating Then throws ConflictException and avoids persistence
  Scenario: Given student not found When updating Then propagates NotFoundException
  Scenario: Given same DNI and course in update request When updating Then reuses existing course and skips validations

Feature: validate
  Scenario: validate
  Scenario: Given existing DNI When validating Then returns true
  Scenario: Given missing DNI When validating Then returns false

Feature: getStudentsByCourseId
  Scenario: getStudentsByCourseId
  Scenario: Given students in course When retrieving Then returns list
  Scenario: Given no students in course When retrieving Then returns empty list

Feature: getByEmail
  Scenario: getByEmail
  Scenario: Given active student exists When retrieving by email Then returns entity
  Scenario: Given student missing When retrieving by email Then throws NotFoundException

Feature: findById
  Scenario: findById
  Scenario: Given active student exists When finding by id Then returns entity
  Scenario: Given student missing When finding by id Then throws NotFoundException
  Scenario: findDeletedById
  Scenario: Given deleted student exists When finding by id Then returns entity
  Scenario: Given deleted student missing When finding by id Then throws NotFoundException

Feature: validate
  Scenario: validate
  Scenario: Given course without students When validating Then does not throw
  Scenario: Given course with students When validating Then throws ConflictException

Feature: getStudentsByIdList
  Scenario: getStudentsByIdList
  Scenario: Given list of ids When retrieving Then returns students preserving order
  Scenario: Given empty ids When retrieving Then returns empty list
