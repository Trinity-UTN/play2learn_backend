# Reporte de pruebas — módulo benefits

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 42.96 %
- Ramas: 39.11 %
- Líneas: 44.08 %
- Complejidad: 38.43 %
- Métodos: 43.31 %

### Módulo `benefits`
- Instrucciones: 84.55 %
- Ramas: 74.03 %
- Líneas: 90.49 %
- Complejidad: 66.2 %
- Métodos: 70.77 %

## Escenarios (services/controllers/dtos)
Feature: PATCH /benefits/teacher/accept-use/{id}
  Scenario: PATCH /benefits/teacher/accept-use/{id}
  Scenario: Given valid purchase ID When accepting use Then returns 200 with purchase response
  Scenario: Given non-existent purchase ID When accepting use Then service throws exception

Feature: DELETE /benefits/teacher/{id}
  Scenario: DELETE /benefits/teacher/{id}
  Scenario: Given valid benefit ID When deleting benefit Then returns 204
  Scenario: Given non-existent benefit ID When deleting benefit Then service throws exception

Feature: POST /benefits
  Scenario: POST /benefits
  Scenario: Given valid request When generating benefit Then returns 201 Created with benefit response
  Scenario: Given invalid request with null name When generating benefit Then returns 400 Bad Request
  Scenario: Given invalid request with null subjectId When generating benefit Then returns 400 Bad Request

Feature: GET /benefits/student/paginated
  Scenario: GET /benefits/student/paginated
  Scenario: Given student with benefits When listing paginated Then returns 200 with paginated data
  Scenario: Given student with no benefits When listing paginated Then returns 200 with empty paginated data

Feature: GET /benefits/teacher
  Scenario: GET /benefits/teacher
  Scenario: Given teacher with benefits When listing benefits Then returns 200 OK with benefits list
  Scenario: Given teacher with no benefits When listing benefits Then returns 200 OK with empty list

Feature: GET /benefits/teacher/paginated
  Scenario: GET /benefits/teacher/paginated
  Scenario: Given teacher with benefits When listing paginated Then returns 200 with paginated data
  Scenario: Given teacher with no benefits When listing paginated Then returns 200 with empty paginated data

Feature: GET /benefits/teacher/purchases/{id}
  Scenario: GET /benefits/teacher/purchases/{id}
  Scenario: Given valid benefit ID with purchases When listing purchases Then returns 200 with list
  Scenario: Given valid benefit ID with no purchases When listing purchases Then returns 200 with empty list
  Scenario: Given non-existent benefit ID When listing purchases Then service throws exception

Feature: GET /benefits/teacher/purchases/paginated/{id}
  Scenario: GET /benefits/teacher/purchases/paginated/{id}
  Scenario: Given valid benefit ID with purchases When listing paginated Then returns 200 with paginated data
  Scenario: Given valid benefit ID with no purchases When listing paginated Then returns 200 with empty paginated data

Feature: GET /benefits/student/used
  Scenario: GET /benefits/student/used
  Scenario: Given student with used purchases When listing used purchases Then returns 200 with list
  Scenario: Given student with no used purchases When listing used purchases Then returns 200 with empty list

Feature: GET /benefits/student/used/paginated
  Scenario: GET /benefits/student/used/paginated
  Scenario: Given student with used purchases When listing paginated Then returns 200 with paginated data
  Scenario: Given student with no used purchases When listing paginated Then returns 200 with empty paginated data

Feature: GET /benefits/teacher/use-requested
  Scenario: GET /benefits/teacher/use-requested
  Scenario: Given teacher with use requested purchases When listing use requested Then returns 200 with list
  Scenario: Given teacher with no use requested purchases When listing use requested Then returns 200 with empty list

Feature: GET /benefits/teacher/use-requested/paginated
  Scenario: GET /benefits/teacher/use-requested/paginated
  Scenario: Given teacher with use requested purchases When listing paginated Then returns 200 with paginated data
  Scenario: Given teacher with no use requested purchases When listing paginated Then returns 200 with empty paginated data

Feature: POST /benefits/student/purchase
  Scenario: POST /benefits/student/purchase
  Scenario: Given valid purchase request When purchasing benefit Then returns 201 Created with purchase response
  Scenario: Given invalid request with null benefitId When purchasing benefit Then returns 400 Bad Request
  Scenario: Given non-existent benefit ID When purchasing benefit Then returns 404 Not Found

Feature: PATCH /benefits/student/request-use/{id}
  Scenario: PATCH /benefits/student/request-use/{id}
  Scenario: Given valid purchase ID When requesting use Then returns 200 with purchase response
  Scenario: Given non-existent purchase ID When requesting use Then service throws exception

Feature: GET /benefits/student/count
  Scenario: GET /benefits/student/count
  Scenario: Given student When counting benefits by state Then returns 200 with count response
  Scenario: Given student with no benefits When counting benefits Then returns 200 with zeros

Feature: BenefitRequestDto validation
  Scenario: BenefitRequestDto validation
  Scenario: When request is valid Then no violations are returned
  Scenario: When name is null Then reports NOT_EMPTY_NAME
  Scenario: When name is blank Then reports NOT_EMPTY_NAME
  Scenario: When name equals max length (100) Then no violations
  Scenario: When name exceeds max length Then reports MAX_LENGTH_NAME_100
  Scenario: When description is null Then reports NOT_EMPTY_DESCRIPTION
  Scenario: When description is blank Then reports NOT_EMPTY_DESCRIPTION
  Scenario: When description equals max length (1000) Then no violations
  Scenario: When description exceeds max length Then reports MAX_LENGTH_DESCRIPTION_1000
  Scenario: When cost is null Then reports NOT_NULL_COST
  Scenario: When cost equals minimum (1) Then no violations
  Scenario: When cost is less than 1 Then reports MIN_COST
  Scenario: When endAt is null Then reports NOT_NULL_END_AT
  Scenario: When endAt is in the past Then reports FUTURE_END_AT
  Scenario: When subjectId is null Then reports NOT_NULL_SUBJECT
  Scenario: When icon is null Then reports NOT_NULL_ICON
  Scenario: When category is null Then reports NOT_NULL_CATEGORY
  Scenario: When color is null Then reports NOT_NULL_COLOR
  Scenario: When purchaseLimit and purchaseLimitPerStudent are null Then no violations (optional fields)
  Scenario: When multiple fields are invalid Then reports all violations

Feature: BenefitPurchaseRequestDto validation
  Scenario: BenefitPurchaseRequestDto validation
  Scenario: When request is valid Then no violations are returned
  Scenario: When benefitId is null Then reports NOT_NULL_BENEFIT_ID

Feature: cu85AcceptBenefitUse
  Scenario: cu85AcceptBenefitUse
  Scenario: Given valid use request When accepting Then updates state to USED and sets usedAt timestamp
  Scenario: Given purchase not found When accepting Then propagates NotFoundException
  Scenario: Given benefit is deleted When accepting Then throws ConflictException
  Scenario: Given teacher is not owner of benefit When accepting Then throws ConflictException
  Scenario: Given benefit is expired When accepting Then throws ConflictException
  Scenario: Given purchase is not in USE_REQUESTED state When accepting Then throws ConflictException

Feature: cu94DeleteBenefit
  Scenario: cu94DeleteBenefit
  Scenario: Given existing benefit and authorized teacher When deleting Then marks as deleted and persists
  Scenario: Given benefit not found When deleting Then propagates NotFoundException
  Scenario: Given teacher is not owner of benefit When deleting Then throws ConflictException
  Scenario: Given benefit with active purchases When deleting Then refunds all active purchases
  Scenario: Given expired benefit When deleting Then skips refund logic and marks as deleted

Feature: cu51GenerateBenefit
  Scenario: cu51GenerateBenefit
  Scenario: Given valid benefit request and authorized teacher When generating Then creates and persists benefit
  Scenario: Given subject is missing When generating benefit Then propagates NotFoundException and stops flow
  Scenario: Given teacher is not assigned to subject When generating benefit Then throws UnauthorizedException and avoids persistence
  Scenario: Given benefit with unlimited purchases When generating Then creates benefit with null purchase limits

Feature: cu80ListBenefitsByStudentPaginated
  Scenario: cu80ListBenefitsByStudentPaginated
  Scenario: Given student with benefits When listing Then returns paginated student benefit DTOs
  Scenario: Given student with state filter When listing Then filters by student state and returns paginated data
  Scenario: Given student with no benefits When listing Then returns empty paginated data
  Scenario: Given invalid state filter value When listing Then ignores filter and returns all benefits

Feature: cu56ListBenefitsPaginated
  Scenario: cu56ListBenefitsPaginated
  Scenario: Given search and filters When listing Then applies specifications and returns paginated data sorted by state
  Scenario: Given no search or filters When listing Then returns paginated data with teacher filter only

Feature: cu55ListBenefitsByTeacher
  Scenario: cu55ListBenefitsByTeacher
  Scenario: Given teacher with benefits When listing Then returns list of benefit DTOs
  Scenario: Given teacher without benefits When listing Then returns empty list

Feature: cu101ListPurchasesPaginated
  Scenario: cu101ListPurchasesPaginated
  Scenario: Given valid benefit and authorized teacher When listing Then returns paginated purchases
  Scenario: Given teacher is not owner of benefit When listing Then throws ConflictException
  Scenario: Given benefit with no purchases When listing Then returns empty paginated data

Feature: cu98ListPurchasesByBenefitId
  Scenario: cu98ListPurchasesByBenefitId
  Scenario: Given valid benefit and authorized teacher When listing Then returns list of purchases
  Scenario: Given benefit not found When listing Then propagates NotFoundException
  Scenario: Given teacher is not owner of benefit When listing Then throws ConflictException
  Scenario: Given benefit with no purchases When listing Then returns empty list

Feature: cu109ListUsedPaginated
  Scenario: cu109ListUsedPaginated
  Scenario: Given student with used benefits When listing Then returns paginated used purchases
  Scenario: Given student with no used benefits When listing Then returns empty paginated data

Feature: cu93ListUsedByStudent
  Scenario: cu93ListUsedByStudent
  Scenario: Given student with used benefits When listing Then returns list of used purchases
  Scenario: Given student with no used benefits When listing Then returns empty list

Feature: cu108ListUseRequestedPaginated
  Scenario: cu108ListUseRequestedPaginated
  Scenario: Given teacher with use requested purchases When listing Then returns paginated use requested purchases
  Scenario: Given teacher with no use requested purchases When listing Then returns empty paginated data

Feature: cu82ListUseRequestedByTeacher
  Scenario: cu82ListUseRequestedByTeacher
  Scenario: Given teacher with use requested purchases When listing Then returns list of use requested purchases
  Scenario: Given teacher with expired or deleted benefits When listing Then skips those benefits
  Scenario: Given teacher with no use requested purchases When listing Then returns empty list

Feature: cu75PurchaseBenefit
  Scenario: cu75PurchaseBenefit
  Scenario: Given valid benefit request and authorized student When purchasing Then creates purchase, generates transaction and decrements limit
  Scenario: Given student not found When purchasing Then propagates NotFoundException and stops flow
  Scenario: Given benefit not found When purchasing Then propagates NotFoundException and stops flow
  Scenario: Given expired benefit When purchasing Then throws ConflictException and avoids persistence
  Scenario: Given student not enrolled in subject When purchasing Then throws ConflictException and avoids persistence
  Scenario: Given student reached purchase limit per student When purchasing Then throws ConflictException and avoids persistence
  Scenario: Given unlimited benefit When purchasing Then creates purchase without limit checks
  Scenario: Given student has purchased but not used benefit When purchasing again Then throws ConflictException and avoids persistence

Feature: cu81RequestBenefitUse
  Scenario: cu81RequestBenefitUse
  Scenario: Given valid benefit purchase When requesting use Then updates state to USE_REQUESTED and persists
  Scenario: Given benefit not found When requesting use Then propagates NotFoundException
  Scenario: Given expired benefit When requesting use Then throws ConflictException
  Scenario: Given student not enrolled in subject When requesting use Then throws ConflictException
  Scenario: Given student has not purchased benefit When requesting use Then throws ConflictException
  Scenario: Given purchase is not in PURCHASED state When requesting use Then throws ConflictException

Feature: cu89CountByStudentState
  Scenario: cu89CountByStudentState
  Scenario: Given student with benefits in different states When counting Then returns correct counts
  Scenario: Given student with no benefits When counting Then returns zeros

Feature: createBenefitStudentDtos
  Scenario: createBenefitStudentDtos
  Scenario: Given list of benefits and student When creating DTOs Then returns list of student DTOs
  Scenario: Given empty list of benefits When creating DTOs Then returns empty list
  Scenario: Given multiple benefits When creating DTOs Then returns DTOs for all benefits
  Scenario: Given benefit with null purchases left When creating DTOs Then sets null in DTO

Feature: filterByStudentState
  Scenario: filterByStudentState
  Scenario: Given benefits and student with AVAILABLE state When filtering Then delegates to AVAILABLE strategy
  Scenario: Given benefits and student with PURCHASED state When filtering Then delegates to PURCHASED strategy
  Scenario: Given benefits and student with USE_REQUESTED state When filtering Then delegates to USE_REQUESTED strategy
  Scenario: Given benefits and student with EXPIRED state When filtering Then delegates to EXPIRED strategy

Feature: getById
  Scenario: getById
  Scenario: Given existing benefit ID When getting by ID Then returns benefit
  Scenario: Given non-existing benefit ID When getting by ID Then throws NotFoundException
  Scenario: Given deleted benefit ID When getting by ID Then throws NotFoundException

Feature: getByStudent
  Scenario: getByStudent
  Scenario: Given student with subjects When getting benefits Then returns benefits for student's subjects
  Scenario: Given student with no subjects When getting benefits Then returns empty list
  Scenario: Given student with multiple subjects When getting benefits Then returns all benefits

Feature: getLastPurchase
  Scenario: getLastPurchase
  Scenario: Given benefit and student with purchases When getting last purchase Then returns last purchase
  Scenario: Given benefit and student with no purchases When getting last purchase Then returns empty optional

Feature: getPurchasesLeftByStudent
  Scenario: getPurchasesLeftByStudent
  Scenario: Given benefit with purchase limit per student and no purchases When getting purchases left Then returns limit
  Scenario: Given benefit with purchase limit per student and some purchases When getting purchases left Then returns remaining
  Scenario: Given benefit with null purchase limit per student When getting purchases left Then returns null
  Scenario: Given benefit with zero purchase limit per student When getting purchases left Then returns null

Feature: getStudentState
  Scenario: getStudentState
  Scenario: Given expired benefit When getting state Then returns EXPIRED
  Scenario: Given benefit with use requested When getting state Then returns USE_REQUESTED
  Scenario: Given purchased benefit When getting state Then returns PURCHASED
  Scenario: Given available benefit When getting state Then returns AVAILABLE

Feature: isPurchased
  Scenario: isPurchased
  Scenario: Given benefit and student with purchased benefit When checking if purchased Then returns true
  Scenario: Given benefit and student with no purchase When checking if purchased Then returns false
  Scenario: Given benefit and student with used purchase When checking if purchased Then returns false
  Scenario: Given benefit and student with use requested purchase When checking if purchased Then returns false

Feature: isUseRequested
  Scenario: isUseRequested
  Scenario: Given benefit and student with use requested purchase When checking if use requested Then returns true
  Scenario: Given benefit and student with no purchase When checking if use requested Then returns false
  Scenario: Given benefit and student with purchased (not use requested) purchase When checking if use requested Then returns false
  Scenario: Given benefit and student with used purchase When checking if use requested Then returns false

Feature: getById
  Scenario: getById
  Scenario: Given existing purchase ID When getting by ID Then returns purchase
  Scenario: Given non-existing purchase ID When getting by ID Then throws NotFoundException
  Scenario: Given deleted purchase ID When getting by ID Then throws NotFoundException

Feature: validateIfPurchasedByStudent
  Scenario: validateIfPurchasedByStudent
  Scenario: Given benefit and student with no purchases When validating Then does not throw exception
  Scenario: Given benefit and student with only used purchases When validating Then does not throw exception
  Scenario: Given benefit and student with purchased (not used) purchase When validating Then throws ConflictException
  Scenario: Given benefit and student with use requested purchase When validating Then throws ConflictException
  Scenario: Given benefit and student with multiple purchases where one is not used When validating Then throws ConflictException

Feature: validatePurchaseLimit
  Scenario: validatePurchaseLimit
  Scenario: Given benefit with purchases left When validating Then does not throw exception
  Scenario: Given benefit with null purchase limit When validating Then does not throw exception
  Scenario: Given benefit with zero purchase limit When validating Then does not throw exception
  Scenario: Given benefit with no purchases left When validating Then throws ConflictException
  Scenario: Given benefit with negative purchases left When validating Then throws ConflictException
