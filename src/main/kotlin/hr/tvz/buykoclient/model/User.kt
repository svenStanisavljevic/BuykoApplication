package hr.tvz.buykoclient.model

import hr.tvz.buykoclient.BuykoApplication

class User(
    var id: Int,
    var username: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var profilePicture: String,
    var password: String,
    var role: Int,
    var phoneNumber: String,
    var balanceEur: Double,
    var location: Location,
) {

    fun adjustCurrency() {
        this.balanceEur = this.balanceEur * BuykoApplication.exchangeRate
    }

}