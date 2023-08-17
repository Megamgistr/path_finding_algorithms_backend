package com.learning.algorithms.dto

data class RequestMatrixNode(
    val id: String,
    val weight: Int = 1,
    val disabled: Boolean = false
)