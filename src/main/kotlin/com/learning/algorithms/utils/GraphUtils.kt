package com.learning.algorithms.utils

import com.learning.algorithms.dto.RequestMatrixNode
import com.learning.algorithms.entities.FindPathGraphNode
import com.learning.algorithms.entities.MazeGenerationGraphEdge
import com.learning.algorithms.entities.MazeGenerationGraphNode

class GraphUtils {
    companion object {
        fun idToNums(id: String): Pair<Int, Int> {
            val nums = id.split("-").map { it.toInt() }
            return Pair(nums.first(), nums.last())
        }

        fun matrixToGraph(matrix: List<List<RequestMatrixNode>>): Map<String, FindPathGraphNode> {
            val nodes = mutableMapOf<String, FindPathGraphNode>()
            for (i in matrix.indices) {
                for (j in matrix[i].indices) {
                    val currentElem = matrix[i][j]
                    val node: FindPathGraphNode = nodes.getOrPut(currentElem.id) {
                        FindPathGraphNode(name = currentElem.id, disabled = currentElem.disabled)
                    }
                    val ajNodes = mutableMapOf<FindPathGraphNode, Int>()
                    if (i + 1 < matrix.size) {
                        val elem = matrix[i + 1][j]
                        ajNodes[nodes.getOrPut(elem.id) { FindPathGraphNode(elem.id, disabled = elem.disabled) }] =
                            elem.weight
                    }
                    if (i - 1 >= 0) {
                        val elem = matrix[i - 1][j]
                        ajNodes[nodes.getOrPut(elem.id) { FindPathGraphNode(elem.id, disabled = elem.disabled) }] =
                            elem.weight
                    }
                    if (j + 1 < matrix[i].size) {
                        val elem = matrix[i][j + 1]
                        ajNodes[nodes.getOrPut(elem.id) { FindPathGraphNode(elem.id, disabled = elem.disabled) }] =
                            elem.weight
                    }
                    if (j - 1 >= 0) {
                        val elem = matrix[i][j - 1]
                        ajNodes[nodes.getOrPut(elem.id) { FindPathGraphNode(elem.id, disabled = elem.disabled) }] =
                            elem.weight
                    }
                    node.adjacentNodes = ajNodes
                }
            }
            return nodes
        }

        fun matrixToGraphWithEdges(matrix: List<List<RequestMatrixNode>>): Pair<MutableSet<MazeGenerationGraphNode>, MutableSet<MazeGenerationGraphEdge>> {
            val nodes = mutableMapOf<String, MazeGenerationGraphNode>()
            val edges = mutableMapOf<String, MazeGenerationGraphEdge>()
            for (i in matrix.indices) {
                for (j in 0..<matrix[i].size) {
                    val currentElem = matrix[i][j]
                    if (i % 2 == 0) {
                        edges.getOrPut(currentElem.id) {
                            MazeGenerationGraphEdge(id = currentElem.id)
                        }
                    } else {
                        if (!edges.containsKey(currentElem.id) && j % 2 != 0) {
                            val node: MazeGenerationGraphNode = nodes.getOrPut(currentElem.id) {
                                MazeGenerationGraphNode(name = currentElem.id)
                            }

                            var elem: RequestMatrixNode
                            var edge: MazeGenerationGraphEdge
                            if (i + 1 < matrix.size) {
                                elem = matrix[i + 1][j]
                                edge = edges.getOrPut(elem.id) { MazeGenerationGraphEdge(id = elem.id) }
                                edge.connectedNodes.add(node)
                                node.bottomEdge = edge
                            }

                            elem = matrix[i - 1][j]
                            edge = edges.getOrPut(elem.id) { MazeGenerationGraphEdge(id = elem.id) }
                            edge.connectedNodes.add(node)
                            node.topEdge = edge

                            if (j + 1 < matrix[i].size) {
                                elem = matrix[i][j + 1]
                                edge = edges.getOrPut(elem.id) { MazeGenerationGraphEdge(id = elem.id) }
                                edge.connectedNodes.add(node)
                                node.rightEdge = edge
                            }

                            elem = matrix[i][j - 1]
                            edge = edges.getOrPut(elem.id) { MazeGenerationGraphEdge(id = elem.id) }
                            edge.connectedNodes.add(node)
                            node.leftEdge = edge
                        }
                    }
                }
            }
            return Pair(nodes.values.toMutableSet(), edges.values.toMutableSet())
        }
    }
}