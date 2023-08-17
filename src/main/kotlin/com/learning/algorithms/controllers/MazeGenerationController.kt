package com.learning.algorithms.controllers

import com.learning.algorithms.dto.maze_generation.MazeGenerationRequest
import com.learning.algorithms.dto.maze_generation.MazeGenerationResponse
import com.learning.algorithms.services.maze_generation.MazeGenerationService
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Flux
import java.time.Duration

@Controller
@RequestMapping("/api/mazeGeneration")
class MazeGenerationController(private val service: MazeGenerationService) {

    @PostMapping(value = ["/"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun mazeGeneration(@RequestBody request: MazeGenerationRequest): Flux<MazeGenerationResponse> {
        val flux = Flux.create<MazeGenerationResponse> { sink ->
            service.subscribe {
                sink.next(it)
            }
            service.generateMaze(request)
        }
        return flux
            .delayElements(Duration.ofMillis(request.delayMilliseconds))
            .takeUntil {
                it.finished
            }
    }
}