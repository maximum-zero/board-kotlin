package com.maximum0.board.controller

import com.maximum0.board.controller.dto.PostCreatedRequest
import com.maximum0.board.controller.dto.PostDetailResponse
import com.maximum0.board.controller.dto.PostSearchRequest
import com.maximum0.board.controller.dto.PostSummaryResponse
import com.maximum0.board.controller.dto.PostUpdatedRequest
import com.maximum0.board.controller.dto.toDto
import com.maximum0.board.controller.dto.toResponse
import com.maximum0.board.service.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val postService: PostService
) {

    @GetMapping("/posts")
    fun getPosts(
        pageable: Pageable,
        postSearchRequest: PostSearchRequest
    ): Page<PostSummaryResponse> {
        return postService.findPageBy(pageable, postSearchRequest.toDto()).toResponse()
    }

    @GetMapping("/posts/{id}")
    fun getPost(
        @PathVariable id: Long
    ): PostDetailResponse {
        return postService.getPost(id).toResponse()
    }

    @PostMapping("/posts")
    fun createPost(
        @RequestBody postCreatedRequest: PostCreatedRequest
    ): Long {
        return postService.createPost(postCreatedRequest.toDto())
    }

    @PutMapping("/posts/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody postUpdatedRequest: PostUpdatedRequest
    ): Long {
        return postService.updatePost(id, postUpdatedRequest.toDto())
    }

    @DeleteMapping("/posts/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @RequestParam createdBy: String
    ): Long {
        return postService.deletePost(id, createdBy)
    }
}
