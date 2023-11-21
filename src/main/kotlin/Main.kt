fun main(args: Array<String>) {
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
    cashier1?.sendOperation(Transaction("87279694200",Currency.USD, 1.0))
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
}