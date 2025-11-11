# Project Preparation Workflow

When a user initiates project preparation with /prepare, execute a comprehensive workflow that transforms an initial project idea into a fully documented, implementable project plan.

## Workflow Overview

This workflow guides users through a systematic 5-step process to transform project ideas into actionable implementation plans:

1. **Brainstorm** - Explore and refine the initial project concept
2. **Plan** - Create high-level implementation architecture and documentation
3. **MVP** - Define a minimal viable product for quick validation
4. **Detail** - Break down implementation into atomic, executable steps
5. **Mermaid** - Generate visual diagrams for system understanding

## Step-by-Step Execution Protocol

### Step 1: Brainstorm Phase (/brainstorm)
**Purpose**: Transform the initial project idea into a structured concept with clear requirements and constraints.

**What happens**:
- Engage in Q&A to explore the project concept deeply
- Identify problem space, requirements, and constraints
- Create foundational documentation: OVERVIEW.md, REQUIREMENTS.md, PLAN.md
- Ensure all key aspects are captured and organized

**Why this step matters**: Without proper brainstorming, subsequent planning may miss critical requirements or misunderstand the core problem being solved.

**User interaction**: Ask clarifying questions, guide conversation, and request feedback on the documented concept before proceeding.

### Step 2: Implementation Planning (/plan)
**Purpose**: Create comprehensive technical architecture and implementation strategy based on the brainstormed requirements.

**What happens**:
- Analyze brainstormed documentation to understand project scope
- Generate IMPLEMENT.md with technical architecture, component design, and system overview
- Create project template files (.gitignore, README.md, requirements.txt, etc.)
- Set up proper directory structure for the project
- Define development setup, testing strategy, and deployment approach

**Why this step matters**: This creates the technical foundation and architectural decisions that will guide all subsequent development work.

**User interaction**: Present the implementation plan and request feedback on technical approach, architecture decisions, and any modifications needed.

### Step 3: MVP Definition (/mvp)
**Purpose**: Define a minimal viable product that demonstrates core value while being achievable quickly.

**What happens**:
- Analyze the detailed implementation plan to identify core components
- Create MVP.md defining the minimal feature set for validation
- Specify relaxed constraints for rapid development
- Define simple user interface (CLI or basic GUI)
- Outline testing strategy and deployment approach for MVP

**Why this step matters**: MVP provides a clear, achievable target for initial development and validation of the project's core value proposition.

**User interaction**: Present the MVP plan and confirm it captures the essential features needed to demonstrate project value.

### Step 4: Detailed Implementation (/detail)
**Purpose**: Break down the implementation into atomic, executable steps that can be systematically completed.

**What happens**:
- Analyze the high-level implementation plan and MVP requirements
- Create DETAILED_IMPLEMENT.md with numbered, atomic steps
- Each step includes objective, prompt, expected output, testing criteria, and dependencies
- Ensure steps can be executed independently and in logical order
- Reference related documentation and diagrams for context

**Why this step matters**: This creates the actionable roadmap that developers can follow to systematically build the project, ensuring nothing is missed.

**User interaction**: Present the detailed implementation plan and confirm the step breakdown is logical and complete.

### Step 5: Visual Documentation (/mermaid)
**Purpose**: Create visual representations of the system architecture and data flow for better understanding and communication.

**What happens**:
- Generate comprehensive Mermaid diagrams in docs/diagrams/
- Create architecture, data-flow, sequence, component-interaction, and state diagrams
- Ensure diagrams are clear, consistent, and properly documented
- Link diagrams to related documentation
- Create diagrams README for easy navigation

**Why this step matters**: Visual documentation helps stakeholders understand the system design, aids in development planning, and serves as reference during implementation.

**User interaction**: Present the generated diagrams and confirm they accurately represent the system design.

## Workflow Execution Rules

### Phase Transitions
- **Automatic progression**: Each phase automatically triggers the next phase
- **User feedback required**: After each phase, request user feedback and confirmation before proceeding
- **Iteration support**: Allow users to request modifications to any phase output before continuing

### Quality Gates
- **Documentation completeness**: Ensure all required documents are created in each phase
- **Consistency check**: Verify that each phase builds logically on the previous phase
- **User approval**: Require explicit user approval before advancing to next phase

### Error Handling
- **Missing dependencies**: If required files are missing, create them or request user input
- **Incomplete information**: If any phase lacks sufficient information, request clarification before proceeding
- **Conflicting requirements**: If conflicts arise between phases, resolve them with user input

## User Experience Guidelines

### Communication Style
- **Clear phase announcements**: Always announce which phase is starting and why
- **Progress updates**: Provide clear summaries of what was accomplished in each phase
- **Next steps preview**: Explain what will happen in the upcoming phase
- **Feedback requests**: Explicitly ask for user feedback and approval at each phase

### Documentation Standards
- **Consistent formatting**: Maintain consistent markdown formatting across all generated documents
- **Cross-references**: Link related documents and diagrams appropriately
- **Clear structure**: Use clear section headers and logical document organization
- **Actionable content**: Ensure all documentation provides actionable guidance

## Success Criteria

The workflow is successful when:
- ✅ All 5 phases complete with user approval
- ✅ Comprehensive documentation exists for project planning and implementation
- ✅ User has clear understanding of project scope, architecture, and implementation approach
- ✅ Project is ready for systematic development following the detailed implementation plan
- ✅ Visual documentation supports understanding and communication of system design

## Usage Example

User: "I am creating a new project that I need to /prepare. The goal of the project is a web-based task management system for remote teams."

**Workflow Response**: 
"I'll guide you through a comprehensive 5-step project preparation workflow. Let's start with Step 1: Brainstorming your task management system concept..."

[Execute brainstorm phase with Q&A]
[Present results and request feedback]
[Proceed to Step 2: Implementation Planning]
[Continue through all 5 phases with user feedback at each step]
