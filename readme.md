<img src="https://i.imgur.com/55JQ2DO.png"></img>


`
Repository with homework for practicing multithreading in Kotlin. A banking system that imitates the real one has been implemented.
`

To download the library, run the following command in the console and open project into IDEA:
```
git clone https://github.com/vSEK1RO/MultiThreading
```

---

## Schedule

* [Example code](https://github.com/vSEK1RO/MultiThreading/tree/main#Example_code)
* [Bank.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#Bank) working with the user, creating cashiers
* [Client.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#Client) contains information about the client and his bills
* [ClientInfo.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#ClientInfo) contains registration information
* [Cashier.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#Cashier) sending operation requests
* [Operation.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#Operation) class from which operations are inherited
* [Currency.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#Currency) enum of currencies
* [Observer.kt:](https://github.com/vSEK1RO/MultiThreading/tree/main#Observer) logging interface
* [FunnyCat:](https://github.com/vSEK1RO/MultiThreading/tree/main#funny_cat) rich cat

---

## [Bank](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Bank.kt)

To start working with a `Bank`, create a bank with its title in the constructor and register a client by passing `ClientInfo`
```c
val bank = Bank("tinkoff")
bank.newObserver(Logger())
val client1 = bank.regClient(ClientInfo(
    "Pupa",
    "Pupovich",
    "88005553535"
))
val client2 = bank.regClient(ClientInfo(
    "Lupa",
    "Lupovich",
    "87279694200"
))
```
Where the newObserver method is used to add logging capability.

To “approach” or "leave" the cash register, use the nextCashier and leaveCashier methods. These methods start the cash register in a new thread.
```kotlin
val cashier1 = bank.nextCashier(client1)
val cashier2 = bank.nextCashier(client2)
```
You can study the list of other functions available for Bank in the file [`Bank.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Bank.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [Client](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Client.kt)

This class is used by other classes and is not intended to be used by the user, but from it you can get information about the user's `bank`, his registration information and `id` in the bank database.
```kotlin
val bank: Bank = client1.bank
val info: ClientInfo = client1.info
val id: Int = client1.id
```
You can study the list of other functions available for Client in the file [`Client.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Client.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [ClientInfo](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/ClientInfo.kt)

Required for registration.
```kotlin
val name: String = info.name
val fullName: String = info.fullName
val telNum: String = info.telNum
```

You can study the list of other functions available for Client in the file [`ClientInfo.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/ClientInfo.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [Cashier](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Cashier.kt)

Allows you to work with clients' money. To execute the request, pass an object of a class that inherits from Operation to the `sendOperation()` method.
```kotlin
cashier1?.sendOperation(OpenBill(Currency.USD))
cashier1?.sendOperation(DepositBill(Currency.USD,3.0))
cashier1?.sendOperation(WithdrawBill(Currency.USD,3.0))
cashier1?.sendOperation(CloseBill(Currency.USD))
```

You can study the list of other functions available for Client in the file [`Cashier.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Cashier.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [Operation](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Operation.kt)

Sealed The class from which all operation classes inherit. The following operations are available.
```kotlin
OpenBill(currency)
CloseBill(currency)
GetBillBalance(currency)
DepositBill(currency, value)
WithdrawBill(currency, value)
Exchange(fromCurr, toCurr, value)
Transaction(telNum, currency, value)
```

You can study the list of other functions available for Client in the file [`Operation.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Operation.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [Currency](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Currency.kt)

Enum class of all currencies. Matches available currencies on openexchangerates.org

You can study the list of other functions available for Client in the file [`Currency.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Currency.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [Observer](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Currency.kt)

The interface from which logging classes are inherited. Has one function `log(info, title, message, operation)`
```kotlin
class Logger : Observer{
	override fun log(info: ClientInfo?, title: String, message: String, operation: String) {
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val formatted = time.format(formatter)
		println("________________________________________\n" +
                "$title:\n\t\t$message\n" +
                "________________________________________\n" +
                "with tel: ${info?.telNum}\n" +
                "client: ${info?.name} ${info?.fullName}\n" +
                "operation: $operation\n" +
                "at the time: $formatted\n")
	}
}
```

You can study the list of other functions available for Client in the file [`Currency.kt`](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Currency.kt)

[:arrow_up:Shedule](https://github.com/vSEK1RO/MultiThreading/tree/main#schedule)
---

---

## [Example code](https://github.com/vSEK1RO/MultiThreading/blob/main/src/main/kotlin/Main.kt)

```kotlin
val bank = Bank("tinkoff")
    bank.newObserver(Logger())
    val client1 = bank.regClient(ClientInfo(
        "Pupa",
        "Pupovich",
        "88005553535"
    ))
    val client2 = bank.regClient(ClientInfo(
        "Lupa",
        "Lupovich",
        "87279694200"
    ))
    //Клиент подходит к кассе, в объекте кассы в конструктор
    //передается ссылка на объект клиента
    val cashier1 = bank.nextCashier(client1)
    val cashier2 = bank.nextCashier(client2)

    //Пул операций свой для каждой кассы
    //Для примера в операции OpenBill добавлена задержка
    //в 5 секунд, чтобы показать что разные кассы работают
    //в разных потоках
    cashier1?.sendOperation(OpenBill(Currency.USD))
    cashier2?.sendOperation(OpenBill(Currency.USD))
    cashier1?.sendOperation(DepositBill(Currency.USD,3.0))

    //Проверка на наличие требуемой суммы
    cashier1?.sendOperation(WithdrawBill(Currency.USD,727.0))

    //Курс валют реальный, парсится с openexchangerates.org
    cashier1?.sendOperation(Transaction("87279694200",Currency.
    USD, 1.0))

    //Проверка на наличие открытого счета у получателя
    cashier1?.sendOperation(Transaction("87279694200",Currency.QAR, 1.0))

    cashier1?.sendOperation(OpenBill(Currency.RUB))
    cashier1?.sendOperation(Exchange(Currency.USD,Currency.RUB,0.5))
    //Тут я показываю что при уходе с кассы отправленные ранее
    //запросы выполнятся, а после чего касса завершит работу
    bank.leaveCashier(client1)
    bank.leaveCashier(client2)

    //Операции отправлены на кассу без клиента
    //Поэтому они не будут выполнены, выполнится логирование
    cashier1?.sendOperation(CloseBill(Currency.RUB))
    cashier1?.sendOperation(CloseBill(Currency.USD))
    
    //После работы с банком можем удалить клиента
    bank.delClient(client1)
    bank.delClient(client2)
```

---

#funny_cat
<img src="https://i.imgur.com/auufGb0.jpeg"></img>