# Domain Events & Persistence System - project-service

This document explains the Domain Events system and the Outbox Pattern persistence implemented in the `project-service`.

## 🏗️ Architecture Implemented

The implementation adheres strictly to the **Hexagonal Architecture** and the **Multi-Tenancy** rules defined for the Prolance platform. The Domain Layer remains completely free of any framework annotations or dependencies.

### 1. Domain Layer (Pure Business Logic)
*   **`DomainEvent` Interface**: A base interface for all events ensuring every event tracks its `eventId`, `tenantId`, `projectId`, and `occurredOn` timestamp.
*   **`AggregateRoot` Base Class**: An abstract class that manages the lifecycle of domain events. It provides methods to `registerEvent()`, `getDomainEvents()`, and `clearDomainEvents()`.
*   **`Project` Aggregate**: Extended to inherit from `AggregateRoot`. It now actively registers events during key business operations.
*   **Event Records**: Events are implemented as immutable Java Records inside `domain/model/event`. The full lifecycle is covered, and **every event tracks the user who initiated the action (`actionBy`)**:
    *   **Project Lifecycle**: `ProjectCreated`, `ProjectUpdated`, `ProjectStatusChanged`, `ProjectDeleted`
    *   **Milestone Lifecycle**: `MilestoneAdded`, `MilestoneUpdated`, `MilestoneCompleted`, `MilestoneDeleted`
    *   **Member Lifecycle**: `MemberAdded`, `MemberRoleUpdated`, `MemberRemoved`

### 2. Application Layer (Ports & Use Cases)
*   **`EventStorePort`**: An outbound port defining the contract for saving a domain event.
*   **`DomainEventPublisher`**: An outbound port defining the contract for publishing events.
*   **`ProjectService`**: Implements the `ProjectUseCase`. The `save` method orchestrates the process: it saves the `Project` entity via the repository, extracts the registered domain events, passes them to the `DomainEventPublisher`, and then clears them from the aggregate. All of this is wrapped in a single `@Transactional` boundary to guarantee atomicity.

### 3. Infrastructure Layer (Adapters)
*   **`OutboxEventEntity`**: A JPA entity mapped to the `outbox_events` table. It stores the serialized event data.
*   **`OutboxEventRepository`**: A Spring Data JPA repository for saving `OutboxEventEntity` objects.
*   **`EventStoreAdapter`**: Implements the `EventStorePort`. It uses Jackson (`ObjectMapper`) to serialize the `DomainEvent` objects into JSON strings and saves them to the database via the `OutboxEventRepository`.
*   **`SpringDomainEventPublisher`**: Implements the `DomainEventPublisher` port. It delegates the persistence to the `EventStoreAdapter` (to save to the DB for auditing/outbox) and also broadcasts the event internally using Spring's `ApplicationEventPublisher`.

---

## 🔄 Execution Flow

1.  **Action**: A domain method is called (e.g., `project.changeStatus(...)`).
2.  **Registration**: Inside the domain method, `registerEvent(new ProjectStatusChanged(...))` is called, adding the event to the aggregate's internal list.
3.  **Persistence**: The `ProjectService.save()` method is invoked.
    *   The `Project` state is saved to its tables.
    *   `project.getDomainEvents()` retrieves the pending events.
    *   `eventPublisher.publish()` is called.
    *   `EventStoreAdapter` serializes the events to JSON and saves them to the `outbox_events` table in the SAME database transaction.
    *   `project.clearDomainEvents()` is called to reset the aggregate.

---

## 🚀 How to Add a New Event

### 1. Define the Event
Create a new Record in `com.dxc.projectservice.domain.model.event` implementing `DomainEvent`:
```java
public record MyCustomEvent(
    UUID eventId, 
    UUID tenantId, 
    UUID projectId, 
    LocalDateTime occurredOn
) implements DomainEvent {
    public static MyCustomEvent now(UUID tenantId, UUID projectId) {
        return new MyCustomEvent(UUID.randomUUID(), tenantId, projectId, LocalDateTime.now());
    }
}
```

### 2. Register the Event in the Aggregate
Inside `Project.java`:
```java
public void performCustomAction() {
    // ... business logic ...
    registerEvent(MyCustomEvent.now(this.tenantId, this.id));
    touch();
}
```

### 3. (Optional) Internal Handling
If you need to react to the event immediately within the same service, create a listener in the infrastructure layer:
```java
@Component
public class MyCustomEventListener {
    @EventListener
    public void handle(MyCustomEvent event) {
        // React to the event (e.g., log it)
    }
}
```

The event will automatically be persisted to the `outbox_events` table as JSON for auditing purposes without any extra work.
