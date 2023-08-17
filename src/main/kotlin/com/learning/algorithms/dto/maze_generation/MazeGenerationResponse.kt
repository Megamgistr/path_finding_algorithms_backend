package com.learning.algorithms.dto.maze_generation

data class MazeGenerationResponse(
    val disabledNodes: List<String> = listOf(),
    val finished: Boolean = false
)
