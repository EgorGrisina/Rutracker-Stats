package dto

data class DTOResponse<out T>(
        val error: DTOError?,
        val result: T?
)
