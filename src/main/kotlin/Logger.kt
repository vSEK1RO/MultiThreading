import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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