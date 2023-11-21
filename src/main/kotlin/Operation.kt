sealed class Operation()

class OpenBill(
    
    val currency: Currency,
) : Operation()

class CloseBill(
    
    val currency: Currency,
) : Operation()

class GetBillBalance(
    
    val currency: Currency,
) : Operation()

class DepositBill(
    
    val currency: Currency,
    val value: Double,
) : Operation()

class WithdrawBill(
    
    val currency: Currency,
    val value: Double,
) : Operation()

class Exchange(
    
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val value: Double,
) : Operation()

class Transaction(
    
    val telNum: String,
    val currency: Currency,
    val value: Double,
) : Operation()

class LeftCashier(

) : Operation()