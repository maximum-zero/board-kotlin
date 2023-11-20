package com.maximum0.board.service

import com.maximum0.board.domain.Comment
import com.maximum0.board.domain.Post
import com.maximum0.board.exception.CommentNotDeletableException
import com.maximum0.board.exception.CommentNotFoundException
import com.maximum0.board.exception.PostNotFoundException
import com.maximum0.board.repository.CommentRepository
import com.maximum0.board.repository.PostRepository
import com.maximum0.board.service.dto.CommentCreateRequestDto
import com.maximum0.board.service.dto.CommentUpdateRequestDto
import com.maximum0.board.service.dto.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository
) {
    @Transactional
    fun createComment(postId: Long, commentCreateRequestDto: CommentCreateRequestDto): Long {
        val post: Post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        return commentRepository.save(commentCreateRequestDto.toEntity(post)).id
    }

    @Transactional
    fun updateComment(id: Long, commentUpdateRequestDto: CommentUpdateRequestDto): Long {
        val comment: Comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        comment.update(commentUpdateRequestDto)
        return comment.id
    }

    @Transactional
    fun deleteComment(id: Long, deletedBy: String): Long {
        val comment: Comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        if (comment.createdBy != deletedBy) {
            throw CommentNotDeletableException()
        }
        commentRepository.delete(comment)
        return id
    }
}
