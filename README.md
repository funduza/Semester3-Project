# Job Platform – 3‑Tier Distributed System

A 3‑tier distributed system developed to help students find jobs and manage job applications efficiently.

This project was developed as part of Semester 3 at VIA University College, aiming to simulate a real-world job portal for both job providers and job seekers.

---

## 🏗️ Architecture

The system follows a 3‑tier architecture:

- **Client Tier**: `Blazor WebAssembly (.NET)`
  - Communicates with the server tier using HTTP REST APIs  
- **Server Tier**: `.NET Web API`
  - Acts as middleware and communicates with the data tier via gRPC  
- **Data Tier**: `Java Spring Boot`
  - Handles all business logic and communicates with a PostgreSQL database

---

## ✨ Features

### 🧑‍💼 Job Provider:
- Create and manage job listings  
- Update company profile  
- View applicants to job posts  
- Update their status

### 👨‍🎓 Job Seeker:
- Browse and filter available jobs using a built-in search bar  
- Apply for jobs directly  
- Create or update personal profiles  
- See their status for the jobs they applied to

### 🔐 Shared Features:
- Authentication & Role-based access  
- View job details  
- Smooth navigation and UI built with Blazor

---

## 🧩 Tech Stack

| Tier   | Technology                         |
|--------|------------------------------------|
| Client | Blazor WebAssembly (.NET)          |
| Server | .NET 8 Web API                     |
| Data   | Java Spring Boot                   |
| DB     | PostgreSQL                         |
| Comm. (S↔D) | gRPC                           |
| Comm. (C↔S) | HTTP REST APIs                 |

---

### ▶️ Getting Started

> Follow the steps below to run the system locally in the correct order.

#### ✅ Prerequisites
Make sure the following are installed:
- .NET 8 SDK  
- Java 17+  
- PostgreSQL  
- Apache Maven  
- gRPC and Protobuf tools (for code generation)

#### 🟢 Run Order
1. **Start the Data Tier** (Java Spring Boot)  
2. **Start the Server Tier** (.NET Web API)  
3. **Start the Client Tier** (Blazor WebAssembly)

Now you’ll see the Blazor client page and can access the platform.
