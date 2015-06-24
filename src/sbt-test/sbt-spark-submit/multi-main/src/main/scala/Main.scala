import java.io._

object Main1 {
  def main (args: Array[String]): Unit = {
    val pw = new PrintWriter(new File("hello1.txt"))
    pw.write("Hello, world")
    pw.close()
  }
}

object Main2 {
  def main(args: Array[String]) {
    val filename = if(args.isEmpty) "hello2.txt" else args.head
    val pw = new PrintWriter(new File(filename))
    pw.write("Hello, world")
    pw.close()
  }
}