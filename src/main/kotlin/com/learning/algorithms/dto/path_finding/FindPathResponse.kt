package com.learning.algorithms.dto.path_finding

data class FindPathResponse(
    val path: List<String> = listOf(),
    val visitedNodes: List<String> = listOf(),
    val finished: Boolean = false
)
