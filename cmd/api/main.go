package main

import (
	"fmt"
	"log"
	"net/http"

	"github.com/jencisoll/vetcontrol-api/internal/handler"
	"github.com/jencisoll/vetcontrol-api/internal/repository"
)

func main() {
	//inicializar dependencias
	ownerRepo := repository.NewOwnerRepository()
	ownerHandler := handler.NewOwnerHandler(ownerRepo)

	//rutas
	http.HandleFunc("/api/v1/owners", ownerHandler.HandleOwners)
	http.HandleFunc("/api/v1/owners/", ownerHandler.HandlerOwnerByID)
	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"status":"ok",  "Service": "vetcontrol-api"}`)
	})
	fmt.Println(" VetControl API corriendo en http://localhost:8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal(err)
	}
}
