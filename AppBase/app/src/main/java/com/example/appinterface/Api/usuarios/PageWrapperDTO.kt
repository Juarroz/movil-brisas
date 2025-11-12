package com.example.appinterface.Api.usuarios

import com.google.gson.annotations.SerializedName

// Este modelo mapea la estructura JSON que genera Spring Data Page
data class PageWrapperDTO(

    // 1. EL CONTENIDO DE LA LISTA
    // Esta clave ("content") DEBE coincidir con el nombre de la propiedad en el JSON de Spring.
    val content: List<UsuarioResponseDTO>,

    // 2. METADATOS DE PAGINACIÓN
    val pageable: PageableDTO, // Objeto que contiene info de la petición (opcional)
    val totalElements: Long,   // Número total de elementos en TODAS las páginas
    val totalPages: Int,       // Número total de páginas
    val last: Boolean,         // Verdadero si es la última página
    val first: Boolean,        // Verdadero si es la primera página
    val number: Int,           // Índice de la página actual (normalmente empieza en 0)
    val size: Int,             // Tamaño de la página (número de elementos solicitados)
    val numberOfElements: Int  // Número de elementos en la página actual
)

// Opcional: Si quieres mapear la clave "pageable" que también devuelve Spring
data class PageableDTO(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean,
    val sort: SortDTO
)

// Opcional: Mapeo de la información de ordenamiento (sort)
data class SortDTO(
    val sorted: Boolean,
    val unsorted: Boolean,
    val empty: Boolean
)