# Airagotes
AI Enhanced Rag supporting notes

## Continuous Integration

This project uses GitHub Actions for continuous integration. The following workflows are set up:

- **Frontend CI**: Runs when changes are made to the UI code
- **Backend CI**: Runs when changes are made to the API code
- **Combined CI**: Runs for all changes to the master branch

For more details, see the [.github/workflows](/.github/workflows) directory.

## Development

### Frontend (UI)

```bash
cd ui
npm install
npm run dev
```

### Backend (API)

```bash
cd api
./gradlew bootRun
```

## Useful Commands

Testing hello endpoint: 

```bash
curl localhost:8080/hello
```
