package com.learning.algorithms.dto.maze_generation

import com.learning.algorithms.dto.RequestMatrixNode
import com.learning.algorithms.entities.MazeGenerationAlgType

data class MazeGenerationRequest(
    val board: List<List<RequestMatrixNode>>,
    val from: RequestMatrixNode,
    val to: RequestMatrixNode,
    val delayMilliseconds: Long,
    val alg: MazeGenerationAlgType
)