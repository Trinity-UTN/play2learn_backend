# Detailed Implementation Plan Generator

When a user initiates detailed implementation with /detail:

1. Analyze the high-level implementation plan (PLAN.md or IMPLEMENT.md) and related docs/ files to identify all major components and subcomponents.

2. Create docs/DETAILED_IMPLEMENT.md with the following structure:

   - For each major component, create a numbered section (e.g., 1.0, 2.0, ...)
   - For each subcomponent or step, create a sub-section (e.g., 1.1, 1.2, ...)
   - Each sub-section must include:
     - **Objective:** What is the goal of this step?
     - **Prompt:** A clear, actionable prompt for the agent or developer to execute this step (e.g., "Implement the Platform Detector module as described.")
     - **Expected Output:** What files, code, or documentation should result?
     - **Testing/Validation:** How to verify the step is complete and correct (unit tests, integration tests, manual checks, etc.)
     - **Dependencies:** Any prerequisites or required context
     - **Notes:** Any additional information, tips, or references

3. Ensure each step is atomic and can be executed independently ("Execute Step 1.1").

4. Reference related documentation, diagrams, and requirements for each step as needed.

5. Maintain clear, consistent formatting and numbering throughout the file.

6. Update DETAILED_IMPLEMENT.md as the project evolves, adding new steps or refining existing ones as needed.

7. Example section format:

   ```markdown
   ## 1.0 Platform Detection
   
   ### 1.1 Implement Platform Detector Module
   - **Objective:** Create a module to detect the Cisco platform from a config file.
   - **Prompt:** Implement the `platform_detector.py` module in `src/core/` to identify IOS, IOS-XE, or NX-OS from input.
   - **Expected Output:** `src/core/platform_detector.py` with detection logic and tests in `tests/unit/test_platform_detector.py`.
   - **Testing/Validation:** Unit tests for various config samples; manual test with sample files.
   - **Dependencies:** None
   - **Notes:** Reference @System Architecture Diagram.
   ```
