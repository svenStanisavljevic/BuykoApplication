package hr.tvz.buykoclient.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hr.tvz.buykoclient.model.Order
import java.io.File

class JsonUtil {

    companion object Companion {
        var idCount = 0;
    }

    private val fileName = "src/main/resources/orders.json"
    private val gson = Gson()
    private val orders: MutableList<Order> = loadOrders()

    private fun loadOrders(): MutableList<Order> {
        val file = File(fileName)
        return if (file.exists() && file.length() > 2) {
            val jsonString = file.readText()
            val listType = object : TypeToken<MutableList<Order>>() {}.type
            val g: MutableList<Order> = gson.fromJson(jsonString, listType)
            Companion.idCount = g.size + 1
            return g
        } else {
            mutableListOf()
        }
    }

    private fun saveOrders() {
        val jsonString = gson.toJson(orders)
        File(fileName).writeText(jsonString)
    }

    fun createOrder(order: Order) {
        orders.add(order)
        idCount++
        saveOrders()
    }

    fun editOrder(orderId: Int, deliveryAddress: String, availableHours: String, paymentType: String) {
        val order = orders.find { it.orderId == orderId }
        if (order != null) {
            order.deliveryAddress = deliveryAddress
            order.availableHours = availableHours
            order.paymentType = paymentType
            saveOrders()
        }
    }

    fun deleteOrder(orderId: Int) {
        val order = orders.find { it.orderId == orderId }
        if (order != null) {
            orders.remove(order)
            saveOrders()
        }
    }

    fun getOrder(orderId: Int): Order? {
        return orders.find { it.orderId == orderId }
    }

    fun getAllOrders(): List<Order> {
        return orders.toList()
    }
}

