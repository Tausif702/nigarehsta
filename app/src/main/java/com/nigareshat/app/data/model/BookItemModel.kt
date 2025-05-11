package com.nigareshat.book.store.model

data class BookItemModel(
    val id: Int,
    var bookName: String= "",
    var quantity: String = "1",
    var price: String = "",
    var discount: String = "",
    var amount: String = ""
)
