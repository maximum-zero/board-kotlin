package com.maximum0.board.controller.dto

import com.maximum0.board.service.dto.PostCreatedRequestDto

data class PostCreatedRequest(
    val title: String,
    val content: String,
    val createdBy: String,
    val tags: List<String> = emptyList()
)

fun PostCreatedRequest.toDto() = PostCreatedRequestDto(
    title = title,
    content = content,
    createdBy = createdBy,
    tags = tags
)
