import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlinx.serialization.json.*
import java.util.Arrays
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock

class Bank(
    var title: String,
){
	var clients = ConcurrentHashMap<Int, Client>()
	var cashiers = ConcurrentHashMap<Int, Cashier>()
    var exRatesToUSD = ConcurrentHashMap<Currency, Double?>()
	var observers = ArrayList<Observer>()
    var lock = ReentrantLock()

    init {
        val executor = ScheduledThreadPoolExecutor(1)
        executor.scheduleAtFixedRate({
			val jsonResponse = getRequest()
			for(currency in Currency.entries) {
                exRatesToUSD[currency] = jsonResponse.jsonObject.get("rates")?.jsonObject?.get(currency.id)?.jsonPrimitive?.double
            }
        },	 0, 1, TimeUnit.MINUTES)
    }

	fun getRequest(): JsonElement{
		val url = URL("https://openexchangerates.org/api/latest.json?app_id=e879f6c6f2fe4ccfb8ee24baea5be75b")
		var res = ""
		val connection = url.openConnection()
		BufferedReader(InputStreamReader(connection.getInputStream())).use { inp ->
			var line: String?
			while (inp.readLine().also { line = it } != null) {
				res+=line
			}
		}
        val obj = Json.parseToJsonElement(res)
        return obj
	}

    fun nextCashier(client: Client?): Cashier?{
        if(client?.bank!=this){
            notifyObservers(client?.info,title,
                "ERROR\n"+
                "The client is not served by this bank",
                        "nextCashier($client)")
            return null
        }
        for(i in cashiers.keys){
            if(cashiers[i]?.client==client) {
                notifyObservers(client.info,title,
                    "The client is being served right now",
                            "nextCashier($client)")
                return null
            }
        }
        var nextId = 0
        while(nextId in cashiers.keys){
            nextId+=1
        }
        val cashier = Cashier(this, client, nextId, "cashier #$nextId")

        cashiers[nextId]=cashier;
        notifyObservers(client.info,title,
            "Client at the ${cashiers[nextId]?.id}'th cashier",
                    "nextCashier($client)")
        cashier.start()
        return cashier
    }

    fun leaveCashier(client: Client?): Unit{
        if(client?.bank!=this){
            notifyObservers(client?.info,title,
                "ERROR\n"+
                "The client is not served by this bank",
                        "leftCashier($client)")
            return
        }
        for(i in cashiers.keys){
            if(cashiers[i]?.client==client){
                cashiers[i]?.sendOperation(LeftCashier())
                cashiers[i]?.join()
                notifyObservers(client.info,title,
                    "The customer have left the ${cashiers[i]?.id}'th cashier",
                            "leftCashier($client)")
                cashiers.remove(i)
                return
            }
        }
        notifyObservers(client.info,title,
            "ERROR\n"+
            "The customer is not at the checkout",
                    "leftCashier($client)")
    }

    fun regClient(info: ClientInfo?): Client?{
        for(i in clients.elements()){
            if(i.info?.telNum==info?.telNum){
                notifyObservers(info,title,
                    "ERROR\n"+
                    "\t\tPhone number is busy",
                            "regClient($info)")
                return null
            }
        }
        var nextId = 0
        while(nextId in clients.keys){
            nextId+=1
        }
        val client = Client(nextId,this,info)
        clients[nextId]=client
        notifyObservers(info,title,
            "You have registered successfully, id: ${client.id}",
                    "regClient($info)")
        return client
    }

    fun delClient(client: Client?): Unit{
        if(client?.bank!=this){
            notifyObservers(client?.info,title,
                "ERROR\n"+
                "The client is not served by this bank",
                        "delClient($client)")
            return
        }
        client.bank=null
        clients.remove(client.id)
        notifyObservers(client.info,title,
            "You have UNREGISTERED successfully",
                    "delClient($client)")
        return
    }

    fun newObserver(observer: Observer): Unit{
        observers.add(observer)
    }

    fun notifyObservers(info: ClientInfo?, title: String, message: String, operation: String): Unit{
        observers.forEach {
            it.log(info,title,message,operation)
        }
    }
}