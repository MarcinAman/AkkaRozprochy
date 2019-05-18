package client

import messages.{OrderRequest, Request, SearchRequest, StreamRequest}

object ClientInputParser {
  def parse(input: String): Either[Error, Request] = input.split(" ", 2) match {
    case Array("order", v) => Right(OrderRequest(v))
    case Array("search", v) => Right(SearchRequest(v))
    case Array("stream", v) => Right(StreamRequest(v))
    case _ => Left(new Error("Couldn't parse input: " + input))
  }
}
