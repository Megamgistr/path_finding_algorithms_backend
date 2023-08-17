package com.learning.algorithms.entities

class MazeGenerationGraphNode(
    val name: String,
    var setNodes: MutableList<MazeGenerationGraphNode> = mutableListOf(),
) {
    lateinit var leftEdge: MazeGenerationGraphEdge
    lateinit var rightEdge: MazeGenerationGraphEdge
    lateinit var topEdge: MazeGenerationGraphEdge
    lateinit var bottomEdge: MazeGenerationGraphEdge
}