package com.maximum0.board.service.dto

data class PostSearchRequestDto(
    val title: String? = null,
    val createdBy: String? = null,
    val tag: String? = null
)
