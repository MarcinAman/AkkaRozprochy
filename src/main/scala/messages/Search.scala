package messages

case class SearchRequest(title: String) extends Request

sealed trait SearchResponse

case class NotFound(title: String) extends SearchResponse

case class Found(title: String, price: Double) extends SearchResponse