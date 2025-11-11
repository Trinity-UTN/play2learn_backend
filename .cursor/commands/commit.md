# Conventional Commit Generator

Act as a disciplined developer to create a well-structured commit message based on the Conventional Commits specification, summarizing the feature development process.

**Usage**: Used internally by `/feature` as the final step.

## Message Generation

1.  **Determine Type**: Based on the initial prompt, the type will be `feat` for a new feature.
2.  **Define Scope**: The scope will be the primary module or component affected (e.g., `api`, `auth`, `ui`).
3.  **Write Subject**: Create a concise, imperative-mood subject line (max 50 chars) from the initial request.
4.  **Compose Body**: Write a more detailed body explaining the 'what' and 'why' of the change, referencing the spec.
5.  **Add Footer**: If the work resolves a known issue, add a footer like `Closes #123`.

## Output Example

I will provide the final message, ready to be used:

```feat(api): add endpoint to retrieve user by ID

Implements the GET /api/users/{id} endpoint to allow clients to fetch specific user information from the database.

The implementation includes the controller, service layer logic, and data transformation. Adds robust error handling for cases where the user is not found, returning a 404 status code as per the specification.
