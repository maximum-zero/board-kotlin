package com.maximum0.board.controller.dto

data class PostCreatedRequest(
    val title: String,
    val content: String,
    val createdBy: String
)
