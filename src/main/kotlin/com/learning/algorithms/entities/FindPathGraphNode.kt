package com.learning.algorithms.entities

class FindPathGraphNode(
    val name: String,
    var minDistance: Int = Int.MAX_VALUE,
    val disabled: Boolean = false,
    var adjacentNodes: MutableMap<FindPathGraphNode, Int> = mutableMapOf(),
    var shortestPath: MutableList<FindPathGraphNode> = mutableListOf(),
)