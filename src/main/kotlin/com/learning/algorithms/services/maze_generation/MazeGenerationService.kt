package com.learning.algorithms.services.maze_generation

import com.learning.algorithms.dto.maze_generation.MazeGenerationRequest
import com.learning.algorithms.dto.maze_generation.MazeGenerationResponse

interface MazeGenerationService {
    fun subscribe(subscriber: (response: MazeGenerationResponse) -> Unit)
    fun generateMaze(request: MazeGenerationRequest)
}