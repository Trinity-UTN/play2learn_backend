# Reporte de pruebas — módulo admin/subject

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 9.95 %
- Ramas: 7.81 %
- Líneas: 9.8 %
- Complejidad: 10.25 %
- Métodos: 12.04 %

### Módulo `admin/subject`
- Instrucciones: 72.28 %
- Ramas: 63.64 %
- Líneas: 69.1 %
- Complejidad: 57.14 %
- Métodos: 59.68 %

## Escenarios (services/controllers/dtos)
Feature: GET /admin/subjects/{id}
  Scenario: GET /admin/subjects/{id}
  Scenario: When subject exists Then returns 200 with payload
  Scenario: GET /admin/subjects
  Scenario: When listing all subjects Then returns 200 with list
  Scenario: When listing subjects by teacher Then returns 200 with filtered list
  Scenario: GET /admin/subjects/paginated
  Scenario: When paginating Then returns paginated response

Feature: POST /admin/subjects
  Scenario: POST /admin/subjects
  Scenario: When subject is registered successfully Then returns 201 and payload
  Scenario: When registration conflicts Then returns 409
  Scenario: PUT /admin/subjects/{id}
  Scenario: When update succeeds Then returns 201 with updated payload
  Scenario: When subject not found Then returns 404
  Scenario: DELETE /admin/subjects/{id}
  Scenario: When deleting existing subject Then returns 204
  Scenario: When delete conflicts (students assigned) Then returns 409
  Scenario: PATCH /admin/subjects/assign-teacher
  Scenario: When assigning teacher Then returns 200 with updated DTO
  Scenario: When teacher not found Then returns 404
  Scenario: PATCH /admin/subjects/unassign-teacher/{subjectId}
  Scenario: When unassigning teacher Then returns 200
  Scenario: When subject not found during unassign Then returns 404

Feature: SubjectRequestDto
  Scenario: SubjectRequestDto
  Scenario: Given valid data When validating Then no violations
  Scenario: Given empty name When validating Then returns not empty violation
  Scenario: Given invalid pattern name When validating Then returns pattern violation
  Scenario: Given null course id When validating Then returns not null violation
  Scenario: Given null optional flag When validating Then returns not null violation
  Scenario: SubjectUpdateRequestDto
  Scenario: Given valid data When validating Then no violations
  Scenario: Given name exceeding max length When validating Then returns size violation
  Scenario: SubjectAssignTeacherRequestDto
  Scenario: Given valid assign request When validating Then no violations
  Scenario: Given missing subjectId When validating Then returns violation
  Scenario: Given missing teacherId When validating Then returns violation

Feature: Add balance execution
  Scenario: Add balance execution
  Scenario: Given positive amount When executing Then increases balance and saves subject
  Scenario: Given zero or negative amount When executing Then throws IllegalArgumentException and skips save

Feature: Add students to subject
  Scenario: Add students to subject
  Scenario: Given subject without targeted students When adding Then appends new students and returns updated DTO
  Scenario: Given subject already containing a student When adding same student Then avoids duplicates
  Scenario: Given empty list of students When adding Then persists without modifications

Feature: Assign teacher to subject
  Scenario: Assign teacher to subject
  Scenario: Given valid subject and teacher When assigning Then persists relation and returns DTO
  Scenario: Given request referencing non existing teacher When assigning Then propagates NotFoundException

Feature: Delete subject flow
  Scenario: Delete subject flow
  Scenario: Given subject without students When deleting Then marks deletedAt and persists changes
  Scenario: Given subject with assigned students When deleting Then throws ConflictException and skips persistence

Feature: Get subject by id
  Scenario: Get subject by id
  Scenario: Given existing subject id When getting Then maps domain entity to DTO

Feature: List subjects paginated
  Scenario: List subjects paginated
  Scenario: Given search and filters When listing Then applies pagination and maps results
  Scenario: Given blank search and mismatched filters When listing Then applies base specification and maps page

Feature: List all subjects
  Scenario: List all subjects
  Scenario: When listing subjects Then delegates to repository ignoring deleted ones and maps DTOs

Feature: List subjects by teacher
  Scenario: List subjects by teacher
  Scenario: Given existing teacher When listing Then returns mapped subjects and queries repository
  Scenario: Given teacher email not registered When listing Then propagates NotFoundException

Feature: SubjectRefillBalanceCronServiceTest
  Scenario: When cron executes Then delegates to refill balance service

Feature: Refill balance execution
  Scenario: Refill balance execution
  Scenario: Given subjects with missing balance When refilling Then generates transactions and returns mapped DTOs
  Scenario: Given subjects already refilled When executing Then skips transaction generation and returns mapped list

Feature: SubjectRegisterServiceTest
  Scenario: Given mandatory subject request with teacher When registering Then assigns course students and initializes balances
  Scenario: Given duplicated subject name in course When registering Then throws ConflictException and aborts persistence
  Scenario: Given subject request referencing missing teacher When registering Then propagates NotFoundException and stops flow

Feature: Remove balance execution
  Scenario: Remove balance execution
  Scenario: Given positive amount When executing Then subtracts balance and saves subject
  Scenario: Given zero or negative amount When executing Then throws IllegalArgumentException and skips save

Feature: Remove students from subject
  Scenario: Remove students from subject
  Scenario: Given subject with matching students When removing Then removes each student and returns DTO
  Scenario: Given subject without requested students When removing Then leaves collection unchanged

Feature: Restore subject flow
  Scenario: Restore subject flow
  Scenario: Given soft deleted subject When restoring Then clears deletedAt and returns mapped DTO

Feature: Unassign teacher from subject
  Scenario: Unassign teacher from subject
  Scenario: Given subject with teacher When unassigning Then clears relation and returns DTO without teacher
  Scenario: Given subject already without teacher When unassigning Then keeps state and persists once

Feature: SubjectUpdateServiceTest
  Scenario: Given existing subject with assigned students When updating Then preserves students and applies new metadata
  Scenario: Given duplicated subject name for same course When updating Then throws ConflictException and avoids persistence
  Scenario: Given update request referencing missing teacher When updating Then throws NotFoundException without saving
