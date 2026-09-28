# Billing Service API Documentation

This document outlines all the endpoints exposed by the `billing-service` along with their corresponding Request/Response DTOs and Enums. This can be used to generate the frontend UI and API client integration.

All endpoints return responses wrapped in a standard `ApiResponse<T>` wrapper (for success responses, it contains the `data` field with the corresponding DTO).

---

## 1. Invoices (`/api/v1/invoices`)

### Endpoints

| Method | Endpoint | Purpose | Request Body / Params | Response Body |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/invoices` | Create a new manual invoice. | `CreateInvoiceRequest` | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/generate` | Generate an invoice from tracked time entries. | `GenerateInvoiceRequest` | `InvoiceResponse` |
| `GET` | `/api/v1/invoices/{invoiceId}` | Get a specific invoice by its ID. | Path: `invoiceId` | `InvoiceResponse` |
| `GET` | `/api/v1/invoices` | Get a paginated list of all invoices. | Query: `page`, `size`, `sort` | `Page<InvoiceSummaryResponse>` |
| `GET` | `/api/v1/invoices/project/{projectId}` | Get a paginated list of invoices for a specific project. | Path: `projectId`, Query: `pageable` | `Page<InvoiceSummaryResponse>` |
| `GET` | `/api/v1/invoices/client/{clientId}` | Get a paginated list of invoices for a specific client. | Path: `clientId`, Query: `pageable` | `Page<InvoiceSummaryResponse>` |
| `PUT` | `/api/v1/invoices/{invoiceId}` | Update invoice details. | Path: `invoiceId`, Body: `UpdateInvoiceRequest` | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/{invoiceId}/line-items` | Add a new line item to an invoice. | Path: `invoiceId`, Body: `AddLineItemRequest` | `InvoiceResponse` |
| `PUT` | `/api/v1/invoices/{invoiceId}/line-items/{lineItemId}` | Update an existing invoice line item. | Path: `invoiceId`, `lineItemId`, Body: `UpdateLineItemRequest`| `InvoiceResponse` |
| `DELETE` | `/api/v1/invoices/{invoiceId}/line-items/{lineItemId}` | Remove a line item from an invoice. | Path: `invoiceId`, `lineItemId` | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/{invoiceId}/send` | Mark the invoice as sent to the client. | Path: `invoiceId` | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/{invoiceId}/pay` | Mark the invoice as fully paid. | Path: `invoiceId`, Query: `comment` (optional) | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/{invoiceId}/partial-pay` | Mark the invoice as partially paid. | Path: `invoiceId`, Query: `comment` (optional) | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/{invoiceId}/cancel` | Cancel an invoice. | Path: `invoiceId`, Query: `reason` (optional) | `InvoiceResponse` |
| `POST` | `/api/v1/invoices/{invoiceId}/pdf` | Generate and attach a PDF document for the invoice. | Path: `invoiceId` | `InvoiceResponse` |
| `DELETE` | `/api/v1/invoices/{invoiceId}` | Delete an invoice. | Path: `invoiceId` | `204 No Content` |

---

## 2. Bill Rates (`/api/v1/bill-rates`)

### Endpoints

| Method | Endpoint | Purpose | Request Body / Params | Response Body |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/bill-rates` | Create a new bill rate for a user on a project. | `CreateBillRateRequest` | `BillRateResponse` |
| `GET` | `/api/v1/bill-rates/{billRateId}` | Get a specific bill rate by its ID. | Path: `billRateId` | `BillRateResponse` |
| `GET` | `/api/v1/bill-rates/project/{projectId}` | Get all bill rates associated with a specific project. | Path: `projectId` | `List<BillRateResponse>` |
| `GET` | `/api/v1/bill-rates/project/{projectId}/user/{userId}` | Get the bill rate for a specific user on a specific project. | Path: `projectId`, `userId` | `BillRateResponse` |
| `PUT` | `/api/v1/bill-rates/{billRateId}` | Update an existing bill rate. | Path: `billRateId`, Body: `UpdateBillRateRequest` | `BillRateResponse` |
| `DELETE` | `/api/v1/bill-rates/{billRateId}` | Delete a bill rate. | Path: `billRateId` | `204 No Content` |

---

## 3. Time Tracking (`/api/v1/time-entries`)

### Endpoints

| Method | Endpoint | Purpose | Request Body / Params | Response Body |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/time-entries` | Log a new time entry. | `LogTimeRequest` | `TimeEntryResponse` |
| `PUT` | `/api/v1/time-entries/{id}` | Update an existing time entry. | Path: `id`, Body: `LogTimeRequest` | `TimeEntryResponse` |
| `DELETE` | `/api/v1/time-entries/{id}` | Delete a time entry. | Path: `id` | `204 No Content` |
| `GET` | `/api/v1/time-entries/project/{projectId}` | Get all time entries for a project within a specific time range. | Path: `projectId`, Query: `start` (ISO DateTime), `end` (ISO DateTime) | `List<TimeEntryResponse>` |

*(Note: Time Tracking responses are returned directly, not wrapped in `ApiResponse`)*

---

## Data Transfer Objects (DTOs)

### Requests (Inputs)

```typescript
// Invoice Requests
interface CreateInvoiceRequest {
  projectId: string; // UUID
  clientId: string; // UUID
  billingType: BillingType;
  issueDate: string; // LocalDate (YYYY-MM-DD)
  dueDate: string; // LocalDate
  periodStartDate?: string; // LocalDate
  periodEndDate?: string; // LocalDate
  taxRate?: number; // BigDecimal
  notes?: string;
}

interface GenerateInvoiceRequest {
  projectId: string; // UUID
  clientId: string; // UUID
  periodStartDate: string; // LocalDate
  periodEndDate: string; // LocalDate
  dueDate: string; // LocalDate
  taxRate?: number;
  notes?: string;
}

interface UpdateInvoiceRequest {
  issueDate?: string; // LocalDate
  dueDate?: string; // LocalDate
  periodStartDate?: string; // LocalDate
  periodEndDate?: string; // LocalDate
  taxRate?: number;
  notes?: string;
}

interface AddLineItemRequest {
  userId?: string; // UUID
  description: string;
  quantity: number;
  unit: LineItemUnit;
  unitPrice: number;
  displayOrder?: number;
}

interface UpdateLineItemRequest {
  description?: string;
  quantity?: number;
  unit?: LineItemUnit;
  unitPrice?: number;
  displayOrder?: number;
}

// Bill Rate Requests
interface CreateBillRateRequest {
  projectId: string; // UUID
  userId: string; // UUID
  seniorityLevel: SeniorityLevel;
  educationLevel: EducationLevel;
  hourlyRate: number; // Positive
  dailyRate?: number;
  effectiveFrom: string; // LocalDate
  effectiveTo?: string; // LocalDate
}

interface UpdateBillRateRequest {
  seniorityLevel?: SeniorityLevel;
  educationLevel?: EducationLevel;
  hourlyRate?: number;
  dailyRate?: number;
  effectiveFrom?: string; // LocalDate
  effectiveTo?: string; // LocalDate
}

// Time Tracking Requests
interface LogTimeRequest {
  projectId: string; // UUID
  taskId?: string; // UUID
  startTime: string; // LocalDateTime (ISO-8601)
  endTime: string; // LocalDateTime (ISO-8601)
  description: string;
  billable: boolean;
}
```

### Responses (Outputs)

```typescript
// Invoice Responses
interface InvoiceResponse {
  id: string; // UUID
  tenantId: string; // UUID
  projectId: string; // UUID
  clientId: string; // UUID
  invoiceNumber: string;
  status: InvoiceStatus;
  billingType: BillingType;
  issueDate: string; // LocalDate
  dueDate: string; // LocalDate
  periodStartDate?: string; // LocalDate
  periodEndDate?: string; // LocalDate
  subtotal: number;
  taxRate: number;
  taxAmount: number;
  totalAmount: number;
  currency: string;
  notes?: string;
  attachmentId?: string; // UUID
  lineItems: InvoiceLineItemResponse[];
  createdBy: string; // UUID
  createdAt: string; // LocalDateTime
  updatedAt?: string; // LocalDateTime
}

interface InvoiceSummaryResponse {
  id: string; // UUID
  projectId: string; // UUID
  clientId: string; // UUID
  invoiceNumber: string;
  status: InvoiceStatus;
  billingType: BillingType;
  issueDate: string; // LocalDate
  dueDate: string; // LocalDate
  totalAmount: number;
  currency: string;
  createdAt: string; // LocalDateTime
}

interface InvoiceLineItemResponse {
  id: string; // UUID
  userId?: string; // UUID
  description: string;
  quantity: number;
  unit: LineItemUnit;
  unitPrice: number;
  lineTotal: number;
  displayOrder: number;
}

// Bill Rate Responses
interface BillRateResponse {
  id: string; // UUID
  projectId: string; // UUID
  userId: string; // UUID
  seniorityLevel: SeniorityLevel;
  educationLevel: EducationLevel;
  hourlyRate: number;
  dailyRate?: number;
  currency: string;
  effectiveFrom: string; // LocalDate
  effectiveTo?: string; // LocalDate
  createdBy: string; // UUID
  createdAt: string; // LocalDateTime
}

// Time Tracking Responses
interface TimeEntryResponse {
  id: string; // UUID
  projectId: string; // UUID
  taskId?: string; // UUID
  userId: string; // UUID
  startTime: string; // LocalDateTime
  endTime: string; // LocalDateTime
  durationMinutes: number;
  description: string;
  billable: boolean;
}
```

---

## Enums

```typescript
enum BillingType {
  HOURLY = 'HOURLY',
  FIXED_PRICE = 'FIXED_PRICE',
  MILESTONE = 'MILESTONE'
}

enum InvoiceStatus {
  DRAFT = 'DRAFT',
  SENT = 'SENT',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE',
  CANCELLED = 'CANCELLED',
  PARTIALLY_PAID = 'PARTIALLY_PAID'
}

enum LineItemUnit {
  HOUR = 'HOUR',
  DAY = 'DAY',
  FIXED = 'FIXED'
}

enum SeniorityLevel {
  JUNIOR = 'JUNIOR',
  CONFIRMED = 'CONFIRMED',
  SENIOR = 'SENIOR',
  LEAD = 'LEAD',
  EXPERT = 'EXPERT'
}

enum EducationLevel {
  BAC_PLUS_2 = 'BAC_PLUS_2',
  BAC_PLUS_3 = 'BAC_PLUS_3',
  BAC_PLUS_5 = 'BAC_PLUS_5',
  BAC_PLUS_7 = 'BAC_PLUS_7',
  OTHER = 'OTHER'
}
```
