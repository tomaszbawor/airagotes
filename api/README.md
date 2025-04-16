# Airagotes - Onion Architecture

This project follows the principles of Onion Architecture, which organizes the codebase into concentric layers with dependencies pointing inward.

## Architecture Overview

The application is structured into concentric layers, from innermost to outermost:

1. **Domain Models** - Core business entities at the center of the architecture
2. **Domain Services** - Core business logic and interfaces (ports)
3. **Application Services** - Orchestrates the flow of data and business rules
4. **Infrastructure Adapters** - Provides implementations for interfaces defined in the domain services layer

### Layer Dependencies

The dependencies between layers follow the Dependency Rule of Onion Architecture:

- Inner layers have no dependencies on outer layers
- All dependencies point inward toward the domain models
- Domain models have no dependencies on other layers
- Domain services depend only on domain models
- Application services depend on domain models and domain services
- Infrastructure adapters depend on domain models, domain services, and application services

## Package Structure

The package structure reflects the Onion Architecture layers:

```
sh.tbawor.airagotes/
├── domain/                  # Domain Layer (innermost)
│   ├── model/              # Domain Models - Core business entities
│   └── port/               # Domain Services - Interfaces defining boundaries
├── application/            # Application Services Layer
│   └── confluence/         # Application-specific services for Confluence
└── infrastructure/         # Infrastructure Adapters Layer (outermost)
    ├── ai/                 # AI model implementations
    ├── config/             # Configuration classes
    ├── confluence/         # Confluence integration adapters
    ├── file/               # File system operation adapters
    ├── persistence/        # Database and vector store adapters
    ├── reranking/          # Reranking implementation adapters
    └── web/                # Web controllers and DTOs
```

## Architecture Rules

The architecture is enforced through ArchUnit tests that validate:

1. **Onion Architecture**: Ensures that dependencies point inward, with domain models at the center
2. **Layer Dependencies**: Domain models have no dependencies on other layers, domain services depend only on domain models, etc.
3. **Ports as Interfaces**: All ports in the domain services layer must be interfaces
4. **Implementation Naming**: Services in the application layer should have the "Service" suffix
5. **Component Placement**: Controllers should reside in the web package
6. **Adapter Implementation**: Infrastructure adapters should implement interfaces from the domain services layer

## Adding New Features

When adding new features to the application, follow the Onion Architecture approach:

1. Start from the center and work outward:
   - Define domain models (entities, value objects) in the domain.model package
   - Define domain services interfaces (ports) in the domain.port package
   - Implement application services that orchestrate the business logic
   - Create infrastructure adapters that implement the domain service interfaces

2. Ensure dependencies always point inward:
   - Domain models should not depend on any other layer
   - Domain services should only depend on domain models
   - Application services can depend on domain models and domain services
   - Infrastructure adapters can depend on all inner layers

3. Add tests to verify:
   - Business logic behavior
   - Architectural compliance using ArchUnit tests

## Testing

The architecture is validated using ArchUnit tests in the `sh.tbawor.airagotes.architecture` package. These tests ensure that the codebase adheres to the Onion Architecture principles, including:

- Proper layering with domain models at the center
- Dependencies pointing inward
- Interfaces in the domain services layer
- Proper implementation of adapters in the infrastructure layer

### Practical Considerations

While the codebase strictly follows Onion Architecture principles, there are a few practical exceptions:

- Configuration classes in the infrastructure layer need to instantiate components from different layers
- Application runners that bootstrap the application may need to access components from multiple layers

These exceptions are explicitly documented and excluded from the architecture validation rules to maintain a pragmatic balance between architectural purity and practical implementation needs.

To run the architecture tests:

```bash
./gradlew test --tests "sh.tbawor.airagotes.architecture.*"
```
