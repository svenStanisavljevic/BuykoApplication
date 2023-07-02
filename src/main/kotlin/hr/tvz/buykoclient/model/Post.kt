package hr.tvz.buykoclient.model

import hr.tvz.buykoclient.BuykoApplication

class Post (
    val id: Int,
    val title: String,
    val description: String,
    var price: Double,
    val category: String,
    var user: User,
){
    fun adjustCurrency() {
        this.price = this.price * BuykoApplication.exchangeRate
    }

    fun buyPost(user: User) {
        this.user = user
    }

}