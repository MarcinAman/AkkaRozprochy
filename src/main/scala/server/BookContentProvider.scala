package server

import java.io.File

class BookContentProvider {
  def provideContent(title: String): Iterator[String] = {
    val path = new File(s"resources/$title.txt")
    val f = scala.io.Source.fromFile(path)

    val lines = f.getLines().toList
    f.close()
    lines.filter(s => !s.isBlank).iterator
  }
}
