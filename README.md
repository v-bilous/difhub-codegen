# DifHub Code Generation tools.
Set of tools for the integration with https://difhub.com. Contains API loader, codegen and IntelliJ Idea plugin. New features and instrumentation coming soon.

Projects:
1. openapi-load – API loader, connects to difhub.com, loads metadata of the selected system and converts it to the OpenAPI documents. 
2. codegen-cli – Codegen, takes OpenAPI specifications, settings, and generates project
3. intellij-idea – Plugin for the IntellijIdea to have UX.

## Installation and usage instructions

NOTE. Since project is in Preview, only usage in development mode available.

For new project:
1. Clone repository of the project
2. Run `./gradlew intellij-plugin:runIde`
3. Select `+ Create New Project` -> `Bootstrap from DifHub` (on left nav)
4. Type your DifHub `Username` and `Password`, `Organization Name` where your system located. 
5. Select required system and change defaults (optional) -> Press Next
6. Choose project name and directory
7. Geneate your project. Progress (or exception) you can see in the console output.

For existing projects (coming soon):


If any issue: 
1. Fix the issue and create PR
2. Post issue here on GitHub
