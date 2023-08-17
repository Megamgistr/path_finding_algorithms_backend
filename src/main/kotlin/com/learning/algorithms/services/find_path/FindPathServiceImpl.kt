package com.learning.algorithms.services.find_path

import com.learning.algorithms.dto.path_finding.FindPathRequest
import com.learning.algorithms.dto.path_finding.FindPathResponse
import com.learning.algorithms.entities.FindPathAlgType
import com.learning.algorithms.entities.FindPathGraphNode
import com.learning.algorithms.utils.GraphUtils.Companion.matrixToGraph
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class FindPathServiceImpl : FindPathService {

    var subscribers: MutableList<(response: FindPathResponse) -> Unit> = mutableListOf()

    override fun subscribe(subscriber: (response: FindPathResponse) -> Unit) {
        subscribers.add(subscriber)
    }

    private fun callSubscribers(end: FindPathGraphNode, visited: Set<FindPathGraphNode>) {
        subscribers.forEach {
            it.invoke(
                FindPathResponse(
                    end.shortestPath.map { g -> g.name },
                    visited.map { g -> g.name })
            )
        }
    }

    override fun findPath(request: FindPathRequest) {
        val nodes = matrixToGraph(request.board)
        val startNode = nodes[request.from.id]!!
        val endNode = nodes[request.to.id]!!
        when (request.alg) {
            FindPathAlgType.DIJKSTRA_SHORTEST -> dijkstraShortestAlg(startNode, endNode, false)
            FindPathAlgType.A_STAR -> dijkstraShortestAlg(startNode, endNode, true)
            FindPathAlgType.DEEP_FIRST -> deepOrBreathFits(startNode, endNode, true)
            FindPathAlgType.BREADTH_FIRST -> deepOrBreathFits(startNode, endNode, false)
        }
    }


    fun deepOrBreathFits(startNode: FindPathGraphNode, endNode: FindPathGraphNode, deepFirst: Boolean) {
        val stack = mutableListOf(startNode)
        val visited = mutableSetOf<FindPathGraphNode>()

        while (stack.isNotEmpty()) {
            var currentNode: FindPathGraphNode
            if (deepFirst) {
                currentNode = stack.last()
                stack.remove(currentNode)
            } else {
                currentNode = stack.removeAt(0)
            }

            if (!currentNode.disabled && !visited.contains(currentNode)) {
                visited.add(currentNode)
                callSubscribers(endNode, visited)
                if (currentNode === endNode) {
                    endNode.shortestPath = visited.toMutableList()
                    callSubscribers(endNode, visited)
                    subscribers.forEach { it.invoke(FindPathResponse(finished = true)) }
                    return
                }
                stack.addAll(currentNode.adjacentNodes.keys)
            }
        }
    }

    fun dijkstraShortestAlg(start: FindPathGraphNode, end: FindPathGraphNode, aStar: Boolean) {
        start.minDistance = 0
        val visited = mutableSetOf<FindPathGraphNode>()
        val unvisited = mutableSetOf(start)
        while (unvisited.isNotEmpty()) {
            val current = if (aStar) getLowestDistanceNode(unvisited, end) else getLowestDistanceNode(unvisited, null)
            unvisited.remove(current)
            if (!current.disabled) {
                for ((adjacentNode, distance) in current.adjacentNodes) {
                    if (!visited.contains(adjacentNode) && !adjacentNode.disabled) {
                        calculateMinimumDistance(adjacentNode, distance, current)
                        unvisited.add(adjacentNode)
                    }
                    if (adjacentNode.name == end.name) {
                        calculateMinimumDistance(adjacentNode, distance, current)
                        end.shortestPath.add(end)
                        callSubscribers(end, visited)
                        subscribers.forEach { it.invoke(FindPathResponse(finished = true)) }
                        return
                    }
                }
                visited.add(current)
                callSubscribers(end, visited)
            } else {
                println(current)
            }
        }
        callSubscribers(end, visited)
        subscribers.forEach { it.invoke(FindPathResponse(finished = true)) }
    }

    private fun calculateMinimumDistance(
        evaluationNode: FindPathGraphNode,
        distance: Int,
        sourceNode: FindPathGraphNode
    ) {
        val sourceDistance = sourceNode.minDistance
        if (sourceDistance + distance < evaluationNode.minDistance) {
            evaluationNode.minDistance = sourceDistance + distance
            val shortestPaths = sourceNode.shortestPath.toMutableList()
            shortestPaths.add(sourceNode)
            evaluationNode.shortestPath = shortestPaths
        }
    }

    private fun getLowestDistanceNode(nodes: Set<FindPathGraphNode>, end: FindPathGraphNode?): FindPathGraphNode {
        var result: FindPathGraphNode? = null
        var lowestDistance = Int.MAX_VALUE
        for (node in nodes) {
            val minDistance =
                if (end != null) calculateHeuristicApproximation(node, end) + node.minDistance else node.minDistance
            if (minDistance < lowestDistance) {
                result = node
                lowestDistance = minDistance
            }
        }
        return result!!
    }

    private fun calculateHeuristicApproximation(node: FindPathGraphNode, end: FindPathGraphNode): Int {
        val (i, j) = node.name.split("-").map { it.toInt() }
        val (k, l) = end.name.split("-").map { it.toInt() }
        val xSum = abs(k - i)
        val ySum = abs(l - j)
        return (xSum + ySum) * 10
    }

}