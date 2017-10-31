package sbtsparksubmit

import scala.util.{Failure, Success, Try}

object RunResult {
  def apply(result: Try[Unit]): Option[String] = result match {
    case Success(_)  => None
    case Failure(ex) => Some(ex.getMessage)
  }
}
