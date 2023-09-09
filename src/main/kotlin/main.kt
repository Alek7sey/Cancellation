import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

//Exception Handling

//Вопрос №7
//Нет. В корутине с областью EmptyCoroutineContext + SupervisorJob() возникает Exception, обработчик исключений отсутствует.
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        CoroutineScope(EmptyCoroutineContext + SupervisorJob()).launch {
            launch {
                delay(1000)
                println("ok") // <--
            }
            launch {
                delay(500)
                println("ok")
            }
            throw Exception("something bad happened")
        }
    }
    Thread.sleep(1000)
}

//Вопрос №6
//Нет. Сработает Exception. Внутри второй/дочерней корутине CoroutineScope у двух ее дочек есть задержки.
// Аварийно завершится родительская корутина, делая отмену выполнения этих дочек. Если бы задержки не было, то println бы отработал.
//fun main() {
//    CoroutineScope(EmptyCoroutineContext).launch {
//        CoroutineScope(EmptyCoroutineContext).launch {
//            launch {
//                delay(1000)
//                println("ok") // <--
//            }
//            launch {
//                delay(500)
//                println("ok")
//            }
//            throw Exception("something bad happened")
//        }
//    }
//    Thread.sleep(1000)
//}

//Вопрос №5
//Да. В supervisorScope исключение в одной дочерней не влияет на другие дочерние и родительскую.
// Поэтому сначала в одной дочерней без задержки Exception  и родительская в try catch обработает исключение, а затем в другой.
//fun main() {
//    CoroutineScope(EmptyCoroutineContext).launch {
//        try {
//            supervisorScope {
//                launch {
//                    delay(500)
//                    throw Exception("something bad happened") // <--
//                }
//                launch {
//                    throw Exception("something bad happened")
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace() // <--
//        }
//    }
//    Thread.sleep(1000)
//}

//Вопрос №4
//Нет. сoroutineScope позволяет создать Scope, который будет перехватывать ошибки во вложенных корутинах и
// предоставлять их в виде Exception. Во второй дочерней без задержки сработал Exception, который обработала родительская.
//fun main() {
//    CoroutineScope(EmptyCoroutineContext).launch {
//        try {
//            coroutineScope {
//                launch {
//                    delay(500)
//                    throw Exception("something bad happened") // <--
//                }
//                launch {
//                    throw Exception("something bad happened")
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    Thread.sleep(1000)
//}

//Вопрос №3
/*Да. В дочерней корутине supervisorScope исключение не влияет на другие дочерние и родительскую.
Поэтому родительская в try catch обработает исключение.*/
//fun main() {
//    CoroutineScope(EmptyCoroutineContext).launch {
//        try {
//            supervisorScope {
//                throw Exception("something bad happened")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace() // <--
//        }
//    }
//    Thread.sleep(1000)
//}

//Вопрос №2
/*Да. coroutineScope позволяет создать Scope, который будет перехватывать ошибки во вложенных корутинах и
выдавать в виде Exception. Родительская корутина обработает в try cach исключение.
StackTrace будет выведен после Exception.*/
//fun main() {
//    CoroutineScope(EmptyCoroutineContext).launch {
//        try {
//            coroutineScope {
//                throw Exception("something bad happened")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace() // <--
//        }
//    }
//    Thread.sleep(1000)
//}

//Вопрос №1
/*Нет. В try catch запускается дочерняя корутина. В ней выбрасывается исключение.
Исключение, выброшенное в дочерней корутине, в родительской перехвачено не будет.*/
//fun main() {
//    with(CoroutineScope(EmptyCoroutineContext)) {
//        try {
//            launch {
//                throw Exception("something bad happened")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace() // <--
//        }
//    }
//    Thread.sleep(1000)
//}

//Cancellation

//Вопрос №2
/*Нет. В основной корутине запускается две дочерних корутины, одна из которых child.
Через 100 мсек child отменяется, в ней println не успеет отработать.
Отмена одной дочерней никак не скажется на работе другой. Затем job ждет завершения дочерней корутины.*/
//fun main() = runBlocking {
//    val job = CoroutineScope(EmptyCoroutineContext).launch {
//        val child = launch {
//            delay(500)
//            println("ok") // <--
//        }
//        launch {
//            delay(500)
//            println("ok")
//        }
//        delay(100)
//        child.cancel()
//    }
//    delay(100)
//    job.join()
//}

//Вопрос №1
/*Нет. В основной корутине запускается две дочерних корутины, но для вывода println задержка больше,
чем задержка при отмене job (job.cancelAndJoin()). У второй дочки тоже большая задержка, она тоже не отработает.*/
//fun main() = runBlocking {
//    val job = CoroutineScope(EmptyCoroutineContext).launch {
//        launch {
//            delay(500)
//            println("ok") // <--
//        }
//        launch {
//            delay(500)
//            println("ok")
//        }
//    }
//    delay(100)
//    job.cancelAndJoin()
//}