package com.example.fintracker.data.remote.dto

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

@JsonAdapter(TransactionMutationRequestJsonAdapter::class)
data class TransactionMutationRequestDto(
    val accountId: Int,
    val categoryId: Int,
    val amount: String,
    val transactionDate: String,
    val comment: String?
)

data class TransactionMutationResponseDto(
    val id: Int?,
    val accountId: Int?,
    val categoryId: Int?,
    val amount: String?,
    val transactionDate: String?,
    val comment: String?,
    val createdAt: String?,
    val updatedAt: String?
)

class TransactionMutationRequestJsonAdapter :
    TypeAdapter<TransactionMutationRequestDto>() {
    override fun write(writer: JsonWriter, source: TransactionMutationRequestDto) {
        // The backend returns 500 when comment is omitted, but accepts an explicit JSON null.
        val serializeNulls = writer.serializeNulls
        writer.serializeNulls = true
        try {
            writer.beginObject()
            writer.name("accountId").value(source.accountId)
            writer.name("categoryId").value(source.categoryId)
            writer.name("amount").value(source.amount)
            writer.name("transactionDate").value(source.transactionDate)
            writer.name("comment").value(source.comment)
            writer.endObject()
        } finally {
            writer.serializeNulls = serializeNulls
        }
    }

    override fun read(reader: JsonReader): TransactionMutationRequestDto =
        throw UnsupportedOperationException("Transaction mutation request is write-only")
}
