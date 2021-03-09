import java.io.OutputStream
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread
import java.io.InputStreamReader

import java.io.BufferedReader





fun main(args: Array<String>) {
    val socket = Socket("localhost", 5000)

   // socket.bind(socket.localSocketAddress)
    socket.sendBufferSize
    PrintWriter(socket.outputStream, true).write("text")


    val out = PrintWriter(socket.getOutputStream(), true)
    val ind = BufferedReader(InputStreamReader(socket.getInputStream()))


}
