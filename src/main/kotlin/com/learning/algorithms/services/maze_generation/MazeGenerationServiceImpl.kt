package com.learning.algorithms.services.maze_generation

import com.learning.algorithms.dto.maze_generation.MazeGenerationRequest
import com.learning.algorithms.dto.maze_generation.MazeGenerationResponse
import com.learning.algorithms.entities.MazeGenerationAlgType
import com.learning.algorithms.entities.MazeGenerationGraphEdge
import com.learning.algorithms.entities.MazeGenerationGraphNode
import com.learning.algorithms.utils.GraphUtils.Companion.idToNums
import com.learning.algorithms.utils.GraphUtils.Companion.matrixToGraphWithEdges
import org.springframework.stereotype.Service
import java.util.*

@Service
class MazeGenerationServiceImpl : MazeGenerationService {

    fun isEdgeStartOrEnd(startId: String, endId: String, edge: MazeGenerationGraphEdge): Boolean {
        return edge.id == startId || edge.id == endId
    }

    var subscribers: MutableList<(response: MazeGenerationResponse) -> Unit> = mutableListOf()
    val random = Random()

    override fun subscribe(subscriber: (response: MazeGenerationResponse) -> Unit) {
        subscribers.add(subscriber)
    }

    private fun callSubscribers(disabled: Set<String>) {
        subscribers.forEach {
            it.invoke(
                MazeGenerationResponse(
                    disabled.toList()
                )
            )
        }
    }

    override fun generateMaze(request: MazeGenerationRequest) {
        val edges = matrixToGraphWithEdges(request.board)
        when (request.alg) {
            MazeGenerationAlgType.KRUSKAL -> kruskalAlg(request.from.id, request.to.id, edges.second)
            MazeGenerationAlgType.SIDEWINDER -> sidewinder(request.from.id, request.to.id,
                edges.second.groupBy { it.id.split("-")[0] }
            )

            MazeGenerationAlgType.RECURSIVE_DIVISION -> startRecursiveDivision(
                edges.second.groupBy { it.id.split("-")[0] } as MutableMap<String, MutableList<MazeGenerationGraphEdge>>,
                request.from.id,
                request.to.id,
                25,
                0,
                request.board[0].size - 1,
                0,
                request.board.size - 1
            )
        }
    }

    fun startRecursiveDivision(
        rows: MutableMap<String, MutableList<MazeGenerationGraphEdge>>,
        startId: String,
        endId: String,
        deep: Int,
        minCol: Int,
        maxCol: Int,
        minRow: Int,
        maxRow: Int,
        disabled: MutableSet<String> = mutableSetOf()
    ) {
        rows[minRow.toString()]!!.forEach {
            disabled.add(it.id)
            callSubscribers(disabled)
        }
        rows.remove(minRow.toString())
        rows[maxRow.toString()]!!.forEach {
            disabled.add(it.id)
            callSubscribers(disabled)
        }
        rows.remove(maxRow.toString())
        rows.values.forEach { it ->
            val first = it.first { it.id.split("-")[1].toInt() == minCol }
            val last = it.first { it.id.split("-")[1].toInt() == maxCol }
            it.remove(first)
            it.remove(last)
            disabled.add(first.id)
            disabled.add(last.id)
            callSubscribers(disabled)
        }
        recursiveDivision(
            rows,
            startId,
            endId,
            deep,
            minCol + 1,
            maxCol - 1,
            minRow + 1,
            maxRow - 1,
            disabled
        )

        subscribers.forEach { it.invoke(MazeGenerationResponse(finished = true)) }
    }

    fun recursiveDivision(
        rows: Map<String, List<MazeGenerationGraphEdge>>,
        startId: String,
        endId: String,
        deep: Int,
        minCol: Int,
        maxCol: Int,
        minRow: Int,
        maxRow: Int,
        disabled: MutableSet<String>
    ) {
        if (deep == 0) return
        if (minCol >= maxCol) return
        if (minRow >= maxRow) return

        val takeRow = random.nextBoolean()

        if (takeRow) {
            var mid = (minRow..<maxRow).random()
            mid = if (mid % 2 == 0) mid else mid + 1
            val row =
                rows[mid.toString()]!!.filter { idToNums(it.id).second in minCol..maxCol }
            val entry = row.filter { idToNums(it.id).second % 2 != 0 }.random()
            for (edge in row) {
                if (edge != entry && !isEdgeStartOrEnd(startId, endId, edge)) {
                    disabled.add(edge.id)
                    callSubscribers(disabled)
                }
            }
            recursiveDivision(rows, startId, endId, deep - 1, minCol, maxCol, minRow, mid - 1, disabled)
            recursiveDivision(rows, startId, endId, deep - 1, minCol, maxCol, mid + 1, maxRow, disabled)
        } else {
            var mid = (minCol..<maxCol).random()
            mid = if (mid % 2 == 0) mid else mid + 1
            val filteredRows = rows.entries.filter { it.key.toInt() in minRow..maxRow }.map { it.value }
            val entry = filteredRows.filter { idToNums(it.first().id).first % 2 != 0 }.random()
            for (row in filteredRows) {
                if (row != entry) {
                    val disabledCell = row.first { idToNums(it.id).second == mid }
                    if (!isEdgeStartOrEnd(startId, endId, disabledCell)) {
                        disabled.add(disabledCell.id)
                        callSubscribers(disabled)
                    }
                }
            }
            recursiveDivision(rows, startId, endId, deep - 1, minCol, mid - 1, minRow, maxRow, disabled)
            recursiveDivision(rows, startId, endId, deep - 1, mid + 1, maxCol, minRow, maxRow, disabled)
        }
    }

    fun sidewinder(startId: String, endId: String, rows: Map<String, List<MazeGenerationGraphEdge>>) {
        val setRun = mutableSetOf<MazeGenerationGraphNode>()
        val disabled = mutableSetOf<String>()
        for (i in rows.entries.indices) {
            val row = rows[i.toString()]!!.sortedBy { it.id.split("-")[1].toInt() }
            for (edge in row) {
                if (edge.connectedNodes.size < 2 && !isEdgeStartOrEnd(startId, endId, edge)) {
                    disabled.add(edge.id)
                    callSubscribers(disabled)
                } else {
                    val leftNode = edge.connectedNodes.firstOrNull() { it.rightEdge === edge }
                    if (leftNode != null) {
                        val currentLine = edge.id.split("-")[0].toInt()
                        if (currentLine == 1) {
                            continue
                        }
                        setRun.add(leftNode)
                        val eastRemove = random.nextBoolean()

                        if (!eastRemove) {
                            val setNode = setRun.random()
                            setRun.forEach {
                                if (it !== setNode) {
                                    if (!isEdgeStartOrEnd(startId, endId, it.topEdge)) {
                                        disabled.add(it.topEdge.id)
                                    }
                                }
                            }
                            if (edge.id != startId && edge.id != endId) {
                                disabled.add(edge.id)
                            }
                            callSubscribers(disabled)
                            setRun.clear()
                        }
                    }
                }
            }
            if (setRun.isNotEmpty()) {
                val setNode = setRun.random()
                setRun.forEach {
                    if (it !== setNode) {
                        disabled.add(it.topEdge.id)
                    }
                }
                setRun.clear()
                callSubscribers(disabled)
            }
        }
        subscribers.forEach { it.invoke(MazeGenerationResponse(finished = true)) }
    }


    fun kruskalAlg(startId: String, endId: String, edges: MutableSet<MazeGenerationGraphEdge>) {
        val disabled = mutableSetOf<String>()

        while (edges.isNotEmpty()) {
            val edge = edges.random()
            edges.remove(edge)
            if (isEdgeStartOrEnd(startId, endId, edge)) continue
            if (edge.connectedNodes.size < 2) {
                disabled.add(edge.id)
                callSubscribers(disabled)
            } else {
                val node1 = edge.connectedNodes.first()
                val node2 = edge.connectedNodes.last()
                if (node1.setNodes.contains(node2)) {
                    disabled.add(edge.id)
                    callSubscribers(disabled)
                } else {
                    node1.setNodes.addAll(node2.setNodes + node2 + node1)
                    for (k in node1.setNodes) {
                        k.setNodes = node1.setNodes
                    }
                }
            }
        }

        subscribers.forEach { it.invoke(MazeGenerationResponse(finished = true)) }
    }
}