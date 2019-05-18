package messages

case class OrderRequest(title: String) extends Request

sealed trait OrderResponse

case class Denial(title: String, reason: String) extends OrderResponse

case class Confirmation(title: String, price: Double) extends OrderResponse
