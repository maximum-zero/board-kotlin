package com.maximum0.board

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController {

    @GetMapping("/sample")
    fun sample(): String {
        return "Sample"
    }

    @PostMapping("/sample")
    fun samplePost(
        @RequestParam name: String
    ): String {
        return "sample $name"
    }
}
