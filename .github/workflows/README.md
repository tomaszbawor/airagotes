# GitHub Actions CI/CD Workflows

This directory contains GitHub Actions workflow configurations for continuous integration (CI) of the AIRagotes project.

## Workflows

### 1. Frontend CI (`frontend-ci.yml`)

This workflow runs when changes are made to files in the `ui/` directory, either via push to the `master` branch or pull requests targeting the `master` branch.

**Steps:**
- Sets up Node.js 20
- Installs dependencies with `npm ci`
- Runs linting with `npm run lint`
- Builds the project with `npm run build`
- Runs tests with `npm test`

### 2. Backend CI (`backend-ci.yml`)

This workflow runs when changes are made to files in the `api/` directory, either via push to the `master` branch or pull requests targeting the `master` branch.

**Steps:**
- Sets up JDK 21 (Temurin distribution)
- Grants execute permission to the Gradle wrapper
- Builds the project with `./gradlew build`
- Runs tests with `./gradlew test`

### 3. Combined CI (`combined-ci.yml`)

This workflow runs for any push to the `master` branch or pull request targeting the `master` branch, regardless of which files were changed. It runs both the frontend and backend jobs to ensure that all tests pass before merging to master.

## Troubleshooting

If a workflow fails, check the GitHub Actions logs for details. Common issues include:

- **Frontend:**
  - Missing dependencies
  - Linting errors
  - TypeScript compilation errors
  - Failed tests

- **Backend:**
  - Java version mismatch (ensure Java 21 is used)
  - Gradle build errors
  - Failed tests

## Local Testing

You can test the build and test processes locally before pushing:

**Frontend:**
```bash
cd ui
npm ci
npm run lint
npm run build
npm test
```

**Backend:**
```bash
cd api
./gradlew build
./gradlew test
```
