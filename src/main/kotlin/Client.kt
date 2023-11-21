import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class Client(
    var id: Int,
    var bank: Bank?,
	var info: ClientInfo?,
) {
    var lock = ReentrantLock()
	private val bills = ConcurrentHashMap<Currency, Double>()

    fun isBillOpened(currency: Currency): Boolean {
        if(currency in bills.keys){
            return true
        }else{
            return false
        }
    }

	fun openBill(currency: Currency): Unit {
		if(currency !in bills.keys){
			bills[currency]=0.0
		}
	}

	fun closeBill(currency: Currency): Unit {
		if(currency in bills.keys){
			bills.remove(currency)
		}
	}

	fun getBillBalance(currency: Currency): Double? {
		return bills[currency]
	}

	fun setBillBalance(currency: Currency, value: Double): Unit {
		if(currency in bills.keys){
			bills[currency]=value
		}
	}
}