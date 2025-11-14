# Reporte de pruebas — módulo admin/teacher

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 15.66 %
- Ramas: 12.4 %
- Líneas: 15.55 %
- Complejidad: 16.42 %
- Métodos: 19.48 %

### Módulo `admin/teacher`
- Instrucciones: 88.52 %
- Ramas: 80.0 %
- Líneas: 89.9 %
- Complejidad: 78.26 %
- Métodos: 83.33 %

## Escenarios (services/controllers/dtos)
Feature: GET /admin/teachers/{id}
  Scenario: GET /admin/teachers/{id}
  Scenario: Should return teacher details with 200 OK
  Scenario: Should return 404 when teacher is not found
  Scenario: GET /admin/teachers
  Scenario: Should return list of teachers with 200 OK
  Scenario: Should return empty list with 200 OK when no teachers exist
  Scenario: GET /admin/teachers/paginated
  Scenario: Should return paginated data with 200 OK
  Scenario: Should return paginated data with 200 OK when no teachers exist

Feature: POST /admin/teachers
  Scenario: POST /admin/teachers
  Scenario: Should create teacher and return 201 with response data
  Scenario: Should return 409 when service throws ConflictException
  Scenario: PUT /admin/teachers/{id}
  Scenario: Should update teacher and return 200 with response data
  Scenario: Should return 404 when teacher not found
  Scenario: DELETE /admin/teachers/{id}
  Scenario: Should delete teacher softly and return 204
  Scenario: Should return 409 when teacher has associated subjects
  Scenario: PATCH /admin/teachers/restore/{id}
  Scenario: Should restore teacher and return 200
  Scenario: Should return 404 when teacher to restore is not found

Feature: TeacherRequestDto validation
  Scenario: TeacherRequestDto validation
  Scenario: Should pass validation with all valid fields
  Scenario: Should fail when name is null
  Scenario: Should fail when lastname exceeds max length
  Scenario: Should fail when email is invalid
  Scenario: Should fail when email exceeds max length
  Scenario: Should fail when dni has invalid format
  Scenario: Should fail with multiple violations when fields blank
  Scenario: Should fail when lastname contains digits
  Scenario: TeacherUpdateDto validation
  Scenario: Should pass validation with all valid fields
  Scenario: Should fail when name contains invalid characters
  Scenario: Should fail when lastname is null
  Scenario: Should fail when dni is null
  Scenario: Should fail when lastname exceeds max length
  Scenario: Should fail when dni has invalid characters

Feature: ✅ Registro exitoso
  Scenario: ✅ Registro exitoso
  Scenario: Debería registrar un docente correctamente con todos los datos anidados
  Scenario: ❌ Validaciones de email
  Scenario: No debe permitir registrar un docente con un email inválido
  Scenario: No debe permitir registrar un docente con email vacío
  Scenario: No debe permitir registrar un docente con email que supere 100 caracteres
  Scenario: ❌ Validaciones de DNI
  Scenario: No debe permitir registrar un docente con puntos en el DNI
  Scenario: No debe permitir registrar un docente con DNI vacío
  Scenario: No debe permitir registrar un docente con DNI que supere 8 dígitos
  Scenario: No debe permitir registrar un docente con DNI que tenga menos de 8 dígitos
  Scenario: ❌ Validaciones de nombre
  Scenario: No debe permitir nombre con caracteres no alfabéticos
  Scenario: Debería permitir nombre con 50 caracteres (límite válido)
  Scenario: No debe permitir nombre con 51 caracteres (límite excedido)
  Scenario: No debe permitir nombre vacío
  Scenario: ❌ Validaciones de apellido
  Scenario: No debe permitir apellido con caracteres no alfabéticos
  Scenario: Debería permitir apellido con 50 caracteres (límite válido)
  Scenario: No debe permitir apellido con 51 caracteres (límite excedido)
  Scenario: No debe permitir apellido vacío

Feature: cu24DeleteTeacher
  Scenario: cu24DeleteTeacher
  Scenario: Given teacher without subjects When deleting Then performs soft delete and persists
  Scenario: Given teacher not found When deleting Then propagates NotFoundException
  Scenario: Given teacher associated to subjects When deleting Then throws ConflictException and avoids save

Feature: TeacherGetServiceTest
  Scenario: Given existing teacher When retrieving by id Then returns mapped response

Feature: cu26ListTeachersPaginated
  Scenario: cu26ListTeachersPaginated
  Scenario: Given search and filters When listing Then returns paginated data with mapped results
  Scenario: Given no search or filters When listing Then returns empty paginated data

Feature: TeacherListServiceTest
  Scenario: Given repository returns teachers When listing Then maps each teacher to response DTO
  Scenario: Given repository returns empty list When listing Then returns empty DTO list

Feature: cu5RegisterTeacher
  Scenario: cu5RegisterTeacher
  Scenario: Given valid request When registering teacher Then validates DNI, creates user and persists teacher
  Scenario: Given duplicate DNI When registering teacher Then throws ConflictException and avoids user creation
  Scenario: Given duplicate user email When registering teacher Then propagates ConflictException and skips persistence

Feature: cu35RestoreTeacher
  Scenario: cu35RestoreTeacher
  Scenario: Given deleted teacher When restoring Then clears deletedAt and returns response DTO
  Scenario: Given teacher not found When restoring Then propagates NotFoundException

Feature: cu23UpdateTeacher
  Scenario: cu23UpdateTeacher
  Scenario: Given valid update request When updating teacher Then persists changes and returns DTO
  Scenario: Given duplicated DNI When updating teacher Then throws ConflictException and avoids persistence
  Scenario: Given teacher not found When updating Then propagates NotFoundException

Feature: validate(dni)
  Scenario: validate(dni)
  Scenario: Given dni unused When validate Then completes without exception
  Scenario: Given dni already used When validate Then throws ConflictException
  Scenario: validate(dni, id)
  Scenario: Given dni unused for other teachers When validate Then completes without exception
  Scenario: Given dni used by another teacher When validate Then throws ConflictException

Feature: getByEmail
  Scenario: getByEmail
  Scenario: Given teacher exists When getByEmail Then returns teacher
  Scenario: Given teacher does not exist When getByEmail Then throws NotFoundException

Feature: findById
  Scenario: findById
  Scenario: Given existing active teacher When findById Then returns teacher
  Scenario: Given missing active teacher When findById Then throws NotFoundException
  Scenario: findDeletedById
  Scenario: Given deleted teacher exists When findDeletedById Then returns teacher
  Scenario: Given deleted teacher missing When findDeletedById Then throws NotFoundException
