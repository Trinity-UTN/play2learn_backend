# Planning Documentation Generator

When a user initiates implementation documentation with /implement:

1. Analyze existing documentation in docs/ to understand project requirements and scope

2. Generate IMPLEMENT.md with the following structure:
   - Technical Architecture
     - System Overview
     - Component Design
     - Data Flow
     - Security Considerations (if applicable)
     - Deployment Architecture
   - Development Setup
     - Environment Requirements
     - Installation Guide
     - Configuration
   - Code Organization
     - Directory Structure
     - Module Design
     - Class Hierarchy
   - Database Design (if applicable)
     - Schema Design
     - Data Models
     - Migration Strategy
   - API Design (if applicable)
     - Endpoints
     - Request/Response Formats
     - Authentication/Authorization
   - Testing Strategy
     - Unit Testing
     - Integration Testing
     - Performance Testing
   - Deployment
     - Build Process
     - Container Configuration (if applicable)
     - Environment Variables
   - Code Quality
     - Style Guide
     - Linting Rules
     - Documentation Standards

3. Create necessary template files:
   - .gitignore
   - README.md
   - requirements.txt
   - setup.py
   - Dockerfile (if applicable)
   - docker-compose.yml (if applicable)
   - .env.example

4. Set up directory structure:
   ```
   project/
   ├── src/
   │   └── [project_name]/
   ├── tests/
   │   ├── unit/
   │   └── integration/
   ├── docs/
   │   ├── diagrams/
   │   └── [other docs]
   ├── config/
   │   └── [config files]
   ├── docker/
   │   └── [docker files]
   └── [root files]
   ```

5. Generate additional documentation as needed:
   - API.md (if applicable)
   - SECURITY.md (if applicable)
   - DEPLOYMENT.md (if applicable)
   - CONTRIBUTING.md
   - CHANGELOG.md

6. Include references to:
   - Existing documentation
   - Generated diagrams
   - External dependencies
   - Development tools

7. Ensure all generated files:
   - Follow consistent formatting
   - Include clear section headers
   - Provide implementation details
   - Reference related documentation
   - Include code examples where appropriate
