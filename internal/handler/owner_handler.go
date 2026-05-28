// El paquete handler normalmente se encarga de recibir peticiones HTTP y responder al cliente.
package handler

import (
	"encoding/json"
	"net/http"
	"strconv"
	"strings"

	"github.com/jencisoll/vetcontrol-api/internal/model"
	"github.com/jencisoll/vetcontrol-api/internal/repository"
)

type OwnerHandler struct {
	repo *repository.OwnerRepository
}

func NewOwnerHandler(repo *repository.OwnerRepository) *OwnerHandler {
	return &OwnerHandler{
		repo: repo,
	}
}
func (h *OwnerHandler) HandleOwners(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	switch r.Method {
	case http.MethodGet:
		json.NewEncoder(w).Encode(h.repo.GetAll())
	case http.MethodPost:
		var req model.CreateOwnerRequest
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			w.WriteHeader(http.StatusBadRequest)
			json.NewEncoder(w).Encode(map[string]string{"error": "JSON invalido"})
			return
		}
		if req.Nombre == "" {
			w.WriteHeader(http.StatusBadRequest)
			json.NewEncoder(w).Encode(map[string]string{"error": "El nombre es requerido"})
			return
		}
		owner := h.repo.Create(req)
		w.WriteHeader(http.StatusCreated)
		json.NewEncoder(w).Encode(owner)
	default:
		w.WriteHeader(http.StatusMethodNotAllowed)

	}
}

func (h *OwnerHandler) HandlerOwnerByID(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	parts := strings.Split(r.URL.Path, "/")
	id, err := strconv.Atoi(parts[len(parts)-1])
	if err != nil {

		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"error": "ID invalido"})
		return
	}
	switch r.Method {
	case http.MethodGet:
		owner, err := h.repo.GetByID(id)
		if err != nil {
			w.WriteHeader(http.StatusNotFound)
			json.NewEncoder(w).Encode(map[string]string{"error": err.Error()})
			return
		}
		json.NewEncoder(w).Encode(owner)
	case http.MethodDelete:
		if err := h.repo.Delete(id); err != nil {
			w.WriteHeader(http.StatusNotFound)
			json.NewEncoder(w).Encode(map[string]string{"error": err.Error()})
			return
		}
		w.WriteHeader(http.StatusNoContent)
	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
	}

}
