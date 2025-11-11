# Backend Critical Analysis Engine

Act as a **principal backend engineer** to analyze a Java Spring Boot feature specification, identify potential downstream problems, and produce a structured JSON report with risks and clarifying questions.

**Usage**: Used after a `/spec` or `/feature` definition to ensure backend stability before implementation.

## Analysis Protocol

### 1. Risk Identification
<thinking>
What could go wrong in the backend? Consider service dependencies, database schema changes, transaction handling, and API consistency.
</thinking>

- **Technical Risks**:  
  - Does the feature modify critical classes annotated with `@Service`, `@Repository`, or `@Entity`?  
  - Are there risks related to transactions, concurrency, or data integrity?  
  - Could this introduce performance bottlenecks (e.g., N+1 queries, unoptimized joins, or excessive I/O operations)?  

- **Scope Risks**:  
  - Is the specification clear about which layers are affected (controller, service, repository)?  
  - Are data contracts (DTOs, responses) clearly defined?  
  - Could the feature’s scope expand due to unclear business rules?

- **Dependency Risks**:  
  - Does this rely on external APIs, microservices, or message brokers (e.g., Kafka, RabbitMQ)?  
  - Are version mismatches or timeout/retry behaviors considered?

### 2. Impact Analysis
<thinking>
If I modify these services or repositories, what other modules might be affected?
</thinking>

- Identify all usages of the modified classes and methods throughout the backend codebase.  
- Determine if any REST controllers, schedulers, or event listeners depend on them.  
- Highlight potential cascading effects that may require regression testing.

### 3. Output Format (JSON)
Produce a JSON report with the following structure:

```json
{
  "technicalRisks": [
    "Possible N+1 query risk in UserRepository",
    "Transactional boundary missing in PaymentService"
  ],
  "scopeRisks": [
    "Ambiguous specification for user details expansion"
  ],
  "dependencyRisks": [
    "Feature depends on external billing API with no fallback"
  ],
  "questions": [
    "Should changes to UserService also update AdminController?",
    "Is there a limit for concurrent requests on this new endpoint?"
  ]
}
```
