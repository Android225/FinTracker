package com.example.fintracker.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionRecord(
    @Embedded val transaction: TransactionEntity,
    @Relation(
        parentColumn = "accountLocalId",
        entityColumn = "localId"
    )
    val account: AccountEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
