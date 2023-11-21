interface Observer {
	fun log(info: ClientInfo?, title: String, message: String, operation: String)
}