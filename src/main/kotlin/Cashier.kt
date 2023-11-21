import java.util.concurrent.LinkedBlockingQueue

class Cashier(
    var bank: Bank,
    var client: Client,
    var id: Int,
    var title: String,
): Thread() {
    var operations = LinkedBlockingQueue<Operation>()
    var operation: Operation? = null
    var _isRunning: Boolean = true

    override fun run(){
        while(_isRunning){
            operation = operations.take()
            if(operation!=null){
                when(operation){
                    is OpenBill -> openBillRun((operation as OpenBill).currency)
                    is CloseBill -> closeBillRun((operation as CloseBill).currency)
                    is GetBillBalance -> getBillBalanceRun((operation as GetBillBalance).currency)
                    is DepositBill -> depositBillRun((operation as DepositBill).currency, (operation as DepositBill).value)
                    is WithdrawBill -> withdrawBillRun((operation as WithdrawBill).currency, (operation as WithdrawBill).value)
                    is Exchange -> exchangeRun((operation as Exchange).fromCurrency, (operation as Exchange).toCurrency, (operation as Exchange).value)
                    is Transaction -> transactionRun((operation as Transaction).telNum, (operation as Transaction).currency, (operation as Transaction).value)
                    is LeftCashier -> _isRunning = false
                    null -> TODO()
                }
                operation=null
            }
        }
    }

    fun sendOperation(operation: Operation): Unit {
        if(this._isRunning == false){
            bank.notifyObservers(client.info,title,
                "ERROR\n"+
                "\t\tYou have already left cashier",
                        "SendOperation($operation)")
            return
        }else {
            operations.put(operation)
        }
    }

    private fun openBillRun(currency: Currency): Unit {
        synchronized(client.lock) {
            Thread.sleep(5000)
            if(client.isBillOpened(currency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${currency.id} bill have already opened",
                            "$operation")
                return
            }
            client.openBill(currency)
            bank.notifyObservers(client.info,title,
                "You have opened ${currency.id} bill",
                        "$operation")
            return
        }
    }

    private fun closeBillRun(currency: Currency): Unit {
        synchronized(client.lock) {
            if(!client.isBillOpened(currency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${currency.id} bill haven't opened",
                            "$operation")
                return
            }
            client.closeBill(currency)
            bank.notifyObservers(client.info,title,
                "You have closed ${currency.id} bill",
                        "$operation"
            )
            return
        }
    }

    private fun getBillBalanceRun(currency: Currency): Unit {
        synchronized(client.lock){
            if(!client.isBillOpened(currency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${currency.id} bill haven't opened",
                            "$operation" )
                return
            }
            var value: Double?
            value = client.getBillBalance(currency)
            bank.notifyObservers(client.info,title,
                "$value ${currency.id} in current bill",
                        "$operation")
            return
        }
    }

    private fun depositBillRun(currency: Currency, value: Double): Unit {
        synchronized(client.lock){
            if(!client.isBillOpened(currency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${currency.id} bill haven't opened",
                            "$operation")
                return
            }
            client.setBillBalance(currency, client.getBillBalance(currency)?.plus(value) ?:value)
            bank.notifyObservers(client.info,title,
                "You have deposit $value ${currency.id} to your bill\n" +
                        "\t\t${String.format("%.2f", client.getBillBalance(currency))} ${currency.id} in current bill",
                        "$operation")
            return
        }
    }

    private fun withdrawBillRun(currency: Currency, value: Double): Unit {
        synchronized(client.lock){
            if(!client.isBillOpened(currency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${currency.id} bill haven't opened",
                            "$operation")
                return
            }
            if(value > (client.getBillBalance(currency) ?: 0.0)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                            "\t\tYou don't have enought money",
                    "$operation")
                return
            }
            client.setBillBalance(currency, client.getBillBalance(currency)?.minus(value) ?:value)
            bank.notifyObservers(client.info,title,
                "You have withdraw $value ${currency.id} from your bill\n" +
                        "\t\t${String.format("%.2f", client.getBillBalance(currency))} ${currency.id} in current bill",
                        "$operation")
            return
        }
    }

    private fun exchangeRun(fromCurrency: Currency, toCurrency: Currency, value: Double): Unit {
        synchronized(client.lock){
            if(!client.isBillOpened(fromCurrency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${fromCurrency.id} bill haven't opened",
                            "$operation")
                return
            }
            if(!client.isBillOpened(toCurrency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${toCurrency.id} bill haven't opened",
                            "$operation")
                return
            }
            if(value > (client.getBillBalance(fromCurrency) ?: 0.0)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\tYou don't have enought money",
                            "$operation")
                return
            }
            client.setBillBalance(fromCurrency, client.getBillBalance(fromCurrency)?.minus(value) ?:value)
            val usdFrom: Double
            val usdTo: Double
            synchronized(bank.lock) {
                usdFrom = bank.exRatesToUSD[fromCurrency] ?: 0.0
                usdTo = bank.exRatesToUSD[toCurrency] ?: 0.0
            }
            val res = value*usdTo/usdFrom
            client.setBillBalance(toCurrency, client.getBillBalance(toCurrency)?.plus(res) ?:res)
            bank.notifyObservers(client.info,title,
                "${String.format("%.2f", client.getBillBalance(toCurrency))} ${toCurrency.id}\n" +
                        "\t\t${String.format("%.2f", client.getBillBalance(fromCurrency))} ${fromCurrency.id} in current bills",
                        "$operation")
            return
        }
    }

    private fun transactionRun(telNum: String, currency: Currency, value: Double): Unit {
        synchronized(client.lock){
            if(!client.isBillOpened(currency)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\t${currency.id} bill haven't opened",
                            "$operation")
                return
            }
            if(value > (client.getBillBalance(currency) ?: 0.0)){
                bank.notifyObservers(client.info,title,
                    "ERROR\n"+
                    "\t\tYou don't have enought money",
                            "$operation")
                return
            }
            val rClient: Client
            for(i in bank.clients.keys){
                if(bank.clients[i]?.info?.telNum == telNum){
                    rClient = bank.clients[i]?:Client(-1,Bank(""),ClientInfo("","",""))
                    if(!rClient.isBillOpened(currency)){
                        bank.notifyObservers(client.info,title,
                            "ERROR\n"+
                            "${currency.id} reciever's bill haven't opened",
                                    "$operation")
                        return
                    }
                    client.setBillBalance(currency, client.getBillBalance(currency)?.minus(value) ?:value)
                    rClient.setBillBalance(currency, rClient.getBillBalance(currency)?.plus(value) ?:value)
                    bank.notifyObservers(client.info,title,
                        "You have sent ${String.format("%.2f", value)} ${currency.id} to ${rClient.info?.name}\n" +
                                "\t\tYour bill balance now is: ${String.format("%.2f", client.getBillBalance(currency))} ${currency.id}",
                                "$operation")
                    return
                }
            }
            bank.notifyObservers(client.info,title,
                "ERROR\n"+
                "Client with this number was not found",
                        "$operation")
            return
        }
    }
}