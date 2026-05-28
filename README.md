# 🐾 VetControl API

REST API para gestión de clínicas veterinarias pequeñas y medianas.  
Construida con **Go** usando arquitectura por capas.

## 🚀 Tech Stack

- **Language:** Go 1.22
- **Router:** net/http (stdlib)
- **Architecture:** Layered (Handler → Repository)
- **Storage:** In-memory (MySQL coming soon)

## 📦 Endpoints

| Method | Route | Description |
|--------|-------|-------------|
| GET | /api/v1/owners | Get all owners |
| GET | /api/v1/owners/{id} | Get owner by ID |
| POST | /api/v1/owners | Create owner |
| DELETE | /api/v1/owners/{id} | Delete owner |
| GET | /health | Health check |

## 🗂️ Project Structure
```bash
vetcontrol-api/
├── cmd/api/          # Entry point
├── internal/
│   ├── handler/      # HTTP handlers
│   ├── model/        # Data structures
│   └── repository/   # Data layer
└── go.mod

## ⚙️ Run Locally

```bash
git clone https://github.com/jencisoll/vetcontrol-api
cd vetcontrol-api
go run cmd/api/main.go

