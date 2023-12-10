package com.maximum0.board.service

import com.maximum0.board.domain.Comment
import com.maximum0.board.domain.Post
import com.maximum0.board.domain.Tag
import com.maximum0.board.exception.PostNotDeletableException
import com.maximum0.board.exception.PostNotFoundException
import com.maximum0.board.exception.PostNotUpdatableException
import com.maximum0.board.repository.CommentRepository
import com.maximum0.board.repository.PostRepository
import com.maximum0.board.repository.TagRepository
import com.maximum0.board.service.dto.PostCreatedRequestDto
import com.maximum0.board.service.dto.PostDetailResponseDto
import com.maximum0.board.service.dto.PostSearchRequestDto
import com.maximum0.board.service.dto.PostUpdatedRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tagRepository: TagRepository,
    private val likeService: LikeService

) : BehaviorSpec({
    beforeSpec {
        postRepository.saveAll(
            listOf(
                Post(title = "제목1", content = "내용1", createdBy = "maximum1", tags = listOf("tag1", "tag2")),
                Post(title = "제목2", content = "내용2", createdBy = "maximum1", tags = listOf("tag1", "tag2")),
                Post(title = "제목3", content = "내용3", createdBy = "maximum1", tags = listOf("tag1", "tag2")),
                Post(title = "제목4", content = "내용4", createdBy = "maximum1", tags = listOf("tag1", "tag2")),
                Post(title = "제목5", content = "내용5", createdBy = "maximum1", tags = listOf("tag1", "tag2")),
                Post(title = "제목6", content = "내용6", createdBy = "maximum0", tags = listOf("tag1", "tag5")),
                Post(title = "제목7", content = "내용7", createdBy = "maximum0", tags = listOf("tag1", "tag5")),
                Post(title = "제목8", content = "내용8", createdBy = "maximum0", tags = listOf("tag1", "tag5")),
                Post(title = "제목9", content = "내용9", createdBy = "maximum0", tags = listOf("tag1", "tag5")),
                Post(title = "제목10", content = "내용10", createdBy = "maximum0", tags = listOf("tag1", "tag5"))
            )
        )
    }

    given("게시글 생성시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreatedRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "maximum0"
                )
            )

            then("게시물이 정상적으로 생성됨을 확인한다.") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "maximum0"
            }
        }

        When("태그가 추가되면") {
            val postId = postService.createPost(
                PostCreatedRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "maximum0",
                    tags = listOf("tag1", "tag2")
                )
            )

            then("태그가 정상적으로 추가됨을 확인한다.") {
                val tags = tagRepository.findByPostId(postId)
                tags.size shouldBe 2
                tags[0].name shouldBe "tag1"
                tags[1].name shouldBe "tag2"
            }
        }
    }

    given("게시글 수정시") {
        val saved = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0", tags = listOf("tag1", "tag2")))

        When("정상 수정시") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdatedRequestDto(
                    title = "update 제목",
                    content = "update 내용",
                    updatedBy = "maximum0"
                )
            )

            then("게시글이 정상적으로 수정됨을 확인한다.") {
                saved.id shouldBe updatedId
                val updated: Post? = postRepository.findByIdOrNull(updatedId)
                updated?.title shouldBe "update 제목"
                updated?.content shouldBe "update 내용"
                updated?.updatedBy shouldBe "maximum0"
            }
        }

        When("게시글이 없을 떄") {
            then("게시글을 찾을 수 없다라는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(
                        9999L,
                        PostUpdatedRequestDto(
                            title = "update 제목",
                            content = "update 내용",
                            updatedBy = "update maximum0"
                        )
                    )
                }
            }
        }

        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다라는 예외가 발생한다.") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        1L,
                        PostUpdatedRequestDto(
                            title = "update 제목",
                            content = "update 내용",
                            updatedBy = "update maximum0"
                        )
                    )
                }
            }
        }

        When("태그가 수정되었을 때") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdatedRequestDto(
                    title = "update 제목",
                    content = "update 내용",
                    updatedBy = "maximum0",
                    tags = listOf("tag1", "tag2", "tag3")
                )
            )

            then("정상적으로 수정됨을 확인한다.") {
                val tags = tagRepository.findByPostId(updatedId)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag3"
            }
            then("태그 순서가 변경되었을때 정상적으로 변경된다.") {
                val updatedId2 = postService.updatePost(
                    saved.id,
                    PostUpdatedRequestDto(
                        title = "update 제목",
                        content = "update 내용",
                        updatedBy = "maximum0",
                        tags = listOf("tag3", "tag2", "tag1")
                    )
                )

                val tags = tagRepository.findByPostId(updatedId2)
                tags.size shouldBe 3
                tags[2].name shouldBe "tag1"
            }
        }
    }

    given("게시글 삭제시") {
        val saved = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))
        When("정상 삭제시") {
            val postId = postService.deletePost(saved.id, "maximum0")
            then("게시글이 정상적으로 삭제됨을 확인한다.") {
                postId shouldBe saved.id
                postRepository.findByIdOrNull(postId) shouldBe null
            }
        }

        When("작성자가 동일하지 않으면") {
            val saved2 = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))
            then("삭제할 수 없는 게시물 입니다라는 예외가 발생한다.") {
                shouldThrow<PostNotDeletableException> {
                    postService.deletePost(saved2.id, "maximum1")
                }
            }
        }
    }

    given("게시글 상세 조회 시") {
        val saved = postRepository.save(Post(title = "제목", content = "내용", createdBy = "maximum0"))
        tagRepository.saveAll(
            listOf(
                Tag(name = "tag1", post = saved, createdBy = "maximum0"),
                Tag(name = "tag2", post = saved, createdBy = "maximum0"),
                Tag(name = "tag3", post = saved, createdBy = "maximum0")
            )
        )

        likeService.createLike(saved.id, "maximum0")
        likeService.createLike(saved.id, "maximum1")
        likeService.createLike(saved.id, "maximum2")

        When("정상 조회 시") {
            val post: PostDetailResponseDto = postService.getPost(saved.id)
            then("게시글이 정상적으로 조회됨을 확인한다.") {
                post.id shouldBe saved.id
                post.title shouldBe "제목"
                post.content shouldBe "내용"
                post.createdBy shouldBe "maximum0"
            }

            then("태그가 정상적으로 조회됨을 확인한다.") {
                post.tags.size shouldBe 3
                post.tags[0] shouldBe "tag1"
                post.tags[1] shouldBe "tag2"
                post.tags[2] shouldBe "tag3"
            }

            then("좋아요 개수가 조회됨을 확인한다.") {
                post.likeCount shouldBe 3
            }
        }

        When("게시글이 없다면") {
            then("게시글을 찾을 수 없습니다라는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.getPost(9999L)
                }
            }
        }

        When("댓글 추가시") {
            commentRepository.save(Comment(content = "댓글 내용1", post = saved, createdBy = "댓글 작성자"))
            commentRepository.save(Comment(content = "댓글 내용2", post = saved, createdBy = "댓글 작성자"))
            commentRepository.save(Comment(content = "댓글 내용3", post = saved, createdBy = "댓글 작성자"))

            val post = postService.getPost(saved.id)
            then("댓글이 함께 조회됨을 확인한다.") {
                post.comments.size shouldBe 3
                post.comments[0].content shouldBe "댓글 내용1"
                post.comments[1].content shouldBe "댓글 내용2"
                post.comments[2].content shouldBe "댓글 내용3"

                post.comments[0].createdBy shouldBe "댓글 작성자"
                post.comments[1].createdBy shouldBe "댓글 작성자"
                post.comments[2].createdBy shouldBe "댓글 작성자"
            }
        }
    }

    given("게시글 목록 조회 시") {
        When("정상 조회 시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글이 정상적으로 조회됨을 확인한다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBeLessThanOrEqual 5
                postPage.content[0].title shouldContain "제목"
                postPage.content[0].createdBy shouldContain "maximum"
            }
        }

        When("타이틀로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "제목1"))
            then("타이틀에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBeLessThanOrEqual 5
                postPage.content[0].title shouldContain "제목1"
                postPage.content[0].createdBy shouldContain "maximum"
            }
        }

        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "maximum1"))
            then("타이틀에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBeLessThanOrEqual 5
                postPage.content[0].title shouldContain "제목"
                postPage.content[0].createdBy shouldContain "maximum"
            }

            then("첫번째 태그가 함께 조회됨을 확인한다.") {
                postPage.content.forEach {
                    it.firstTag shouldBe "tag1"
                }
            }
        }

        When("태그로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            then("태그에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldBe "제목10"
                postPage.content[1].title shouldBe "제목9"
                postPage.content[2].title shouldBe "제목8"
                postPage.content[3].title shouldBe "제목7"
                postPage.content[4].title shouldBe "제목6"
            }
        }
        When("좋아요가 2개 추가되었을 때") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            postPage.content.forEach {
                likeService.createLike(it.id, "maximum0")
                likeService.createLike(it.id, "maximum1")
            }

            val likedPostPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(tag = "tag5"))
            then("좋아요 개수가 정상적으로 조회됨을 확인한다.") {
                likedPostPage.content.forEach {
                    it.likeCount shouldBe 2
                }
            }
        }
    }
})
