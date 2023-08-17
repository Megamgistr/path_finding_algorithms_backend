package com.learning.algorithms.services.find_path

import com.learning.algorithms.dto.path_finding.FindPathRequest
import com.learning.algorithms.dto.path_finding.FindPathResponse

interface FindPathService {
    fun subscribe(subscriber: (response: FindPathResponse) -> Unit)
    fun findPath(request: FindPathRequest)
}