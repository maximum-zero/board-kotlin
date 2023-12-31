package com.maximum0.board.service.dto

import com.maximum0.board.domain.Post

data class PostCreatedRequestDto(
    val title: String,
    val content: String,
    val createdBy: String,
    val tags: List<String> = emptyList()
)

fun PostCreatedRequestDto.toEntity() = Post(
    title = title,
    content = content,
    createdBy = createdBy,
    tags = tags
)
