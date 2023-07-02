package hr.tvz.buykoclient.model

class Order (
    val orderId: Int,
    val postId: Int,
    val sellerId: Int,
    val timestamp: String,
    var deliveryAddress: String,
    var availableHours: String,
    var paymentType: String,
)