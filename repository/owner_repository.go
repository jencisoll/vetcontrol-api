package repository

import (
	"errors"
	"sync"
	"time"

	"github.com/jencisoll/vetcontrol-api/internal/model"
)

//OwnerRepository maneja el almacenamiento de dueños

type OwnerRepository struct {
	mu     sync.RWMutex
	owners []model.Owner
	nextID int
}

// NewOwnerRepository crea un repositorio nuevo
func NewOwnerRepository() *OwnerRepository {
	return &OwnerRepository{nextID: 1}
}

// GetAll devuelve todos los dueños
func (r *OwnerRepository) GetAll() []model.Owner {
	r.mu.RLock()
	defer r.mu.RUnlock()
	return r.owners
}

// GetByID busca un dueño por ID
func (r *OwnerRepository) GetByID(id int) (model.Owner, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	for _, o := range r.owners {
		if o.ID == id {
			return o, nil
		}
	}
	return model.Owner{}, errors.New("Dueño no encontrado")
}

// Create agrega un nuevo dueño
func (r *OwnerRepository) Create(req model.CreateOwnerRequest) model.Owner {
	r.mu.Lock()
	defer r.mu.Unlock()
	owner := model.Owner{
		ID:        r.nextID,
		Nombre:    req.Nombre,
		Telefono:  req.Telefono,
		Email:     req.Email,
		Direccion: req.Direccion,
		CreatedAt: time.Now(),
	}
	r.nextID++
	r.owners = append(r.owners, owner)
	return owner
}

// Delete elimina un dueño por ID
func (r *OwnerRepository) Delete(id int) error {
	r.mu.Lock()
	defer r.mu.Unlock()
	for i, o := range r.owners {
		if o.ID == id {
			r.owners = append(r.owners[:i], r.owners[i+1:]...)
			return nil
		}
	}
	return errors.New("No encontrado")
}
