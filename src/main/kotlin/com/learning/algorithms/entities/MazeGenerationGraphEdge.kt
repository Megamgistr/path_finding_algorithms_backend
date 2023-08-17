package com.learning.algorithms.entities

class MazeGenerationGraphEdge(
    val id: String,
    val isDisabled: Boolean = true,
    var weight: Int = 1,
    var connectedNodes: MutableSet<MazeGenerationGraphNode> = mutableSetOf()
)