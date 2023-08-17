package com.learning.algorithms.controllers

import com.learning.algorithms.dto.path_finding.FindPathRequest
import com.learning.algorithms.dto.path_finding.FindPathResponse
import com.learning.algorithms.services.find_path.FindPathService
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Flux
import java.time.Duration

@Controller
@RequestMapping("/api/findPath")
class FindPathController(private val findPathService: FindPathService) {

    @PostMapping(value = ["/"], produces = [MediaType.APPLICATION_STREAM_JSON_VALUE])
    fun getPath(@RequestBody request: FindPathRequest): Flux<FindPathResponse> {
        val flux = Flux.create<FindPathResponse> { sink ->
            findPathService.subscribe {
                sink.next(it)
            }
            findPathService.findPath(request)
        }
        return flux
            .delayElements(Duration.ofMillis(request.delayMilliseconds))
            .takeUntil {
                it.finished
            }
    }
}