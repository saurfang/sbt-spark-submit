import java.io._

object Main {
  def main(args: Array[String]) {
    val filename = s"foo${args.mkString}.txt"
    val pw = new PrintWriter(new File(filename))
    pw.write("Hello, world")
    pw.close()
  }
}