package com.example.fintracker.data.remote.mapper

import com.example.fintracker.data.remote.dto.CategoryDto
import com.example.fintracker.domain.Category

internal fun CategoryDto.toDomain(): Category = Category(
    id = id.requireRemoteField("category.id"),
    name = name.requireRemoteField("category.name"),
    emoji = emoji.requireRemoteField("category.emoji"),
    isIncome = isIncome.requireRemoteField("category.isIncome")
)
