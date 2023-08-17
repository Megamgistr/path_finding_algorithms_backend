package com.learning.algorithms.dto.path_finding

import com.learning.algorithms.dto.RequestMatrixNode
import com.learning.algorithms.entities.FindPathAlgType

data class FindPathRequest(
    val board: List<List<RequestMatrixNode>>,
    val from: RequestMatrixNode,
    val to: RequestMatrixNode,
    val delayMilliseconds: Long,
    val alg: FindPathAlgType
)