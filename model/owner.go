package model

import "time"

// Owner representa al dueño de un mascota en el sistema
type Owner struct {
	ID        int    `json:"id"`
	Nombre    string `json:"nombre"`
	Telefono  string `json:"telefono"`
	Email     string `json:"email"`
	Direccion string `json:"direccion,omitempty"` // omitempty = no aparece si  está vacío

	CreatedAt time.Time `json:"created_at"`
}

// CreateOwnerRequest es lo que recibe el API cuando crean un dueño
type CreateOwnerRequest struct {
	Nombre    string `json:"nombre"`
	Telefono  string `json:"telefono"`
	Email     string `json:"email"`
	Direccion string `json:"direccion"`
}
