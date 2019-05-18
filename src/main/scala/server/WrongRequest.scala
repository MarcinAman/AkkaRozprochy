package server

final case class WrongRequest(private val message: String = "",
                              private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
