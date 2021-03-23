package example.demo

import java.io.IOException
import java.io.PrintWriter
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PayaraDemoServlet : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    public override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        res.contentType = "text/html; charset=UTF-8"
        val out: PrintWriter = res.writer
        out.println("hello world.")
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}