package com.maximum0.board.controller.dto

import com.maximum0.board.service.dto.PostUpdatedRequestDto

data class PostUpdatedRequest(
    val title: String,
    val content: String,
    val updatedBy: String,
    val tags: List<String> = emptyList()
)

fun PostUpdatedRequest.toDto() = PostUpdatedRequestDto(
    title = title,
    content = content,
    updatedBy = updatedBy,
    tags = tags
)
