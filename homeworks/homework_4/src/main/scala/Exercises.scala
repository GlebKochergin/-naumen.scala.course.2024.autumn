
object Exercises {
    import scala.annotation.tailrec
    import scala.util.Random

    /**
     * Задание №1
     * Дана императивная функция findSumImperative.
     * Напишите ее аналог (findSumFunctional) в функциональном стиле.
     *
     * ПОДСКАЗКА
     * Стоит воспользоваться методами, которые предоставляет объект List или рекурсией.
     * Страница с полезностями List: https://alvinalexander.com/scala/list-class-methods-examples-syntax/
     */
    def findSumImperative(items: List[Int], sumValue: Int): (Int, Int) = {
        var result: (Int, Int) = (-1, -1)
        for (i <- 0 until items.length) {
            for (j <- 0 until items.length) {
                if (items(i) + items(j) == sumValue && i != j) {
                    result = (i, j)
                }
            }
        }
        result
    }

    def findSumFunctional(items: List[Int], sumValue: Int): (Int, Int) = {
        items.zipWithIndex
          .flatMap { case (x, i) =>
              items.zipWithIndex.collect {
                  case (y, j) if i != j && x + y == sumValue => (i, j)
              }
          }
          .lastOption
          .getOrElse((-1, -1))
    }


    /**
     * Задание №2
     *
     * Дана рекурсивная функция simpleRecursion.
     * Перепишите ее так, чтобы получилась хвостовая рекурсивная функция.
     *
     * Для прохождения теста на большое количество элементов в списке
     * используйте анотацию @tailrec к вашей функции.
     */
    def simpleRecursion(items: List[Int], index: Int = 1): Int = {
        items match {
            case head :: tail =>
                if (head % 2 == 0) {
                    head * simpleRecursion(tail, index + 1) + index
                } else {
                    -1 * head * simpleRecursion(tail, index + 1) + index
                }
            case _ => 1
        }
    }

    def tailRecRecursion(items: List[Int]): Int = {
        @tailrec
        def helper(items: List[Int], index: Int, accumulator: Int): Int = {
            items match {
                case head :: tail =>
                    val updatedAccumulator =
                        if (head % 2 == 0) {
                            accumulator * head + index
                        } else {
                            accumulator * -1 * head + index
                        }
                    helper(tail, index - 1, updatedAccumulator)
                case Nil => accumulator
            }
        }

        helper(items.reverse, items.size, 1)
    }

    /**
     * Задание №3
     * Реализуйте алгоритм бинарного поиска, который соответсвует всем правилам функционального программирования.
     * Необходимо возвращать индекс соответствующего элемента в массиве
     * Если ответ найден, то возвращается Some(index), если нет, то None
     */

    def functionalBinarySearch(items: List[Int], value: Int): Option[Int] = {
        @tailrec
        def binarySearchRecursive(left: Int, right: Int): Option[Int] = {
            if (left > right) {
                None
            } else {
                val mid = left + (right - left) / 2
                items(mid) match {
                    case x if x == value => Some(mid)
                    case x if x < value  => binarySearchRecursive(mid + 1, right)
                    case x if x > value  => binarySearchRecursive(left, mid - 1)
                }
            }
        }

        binarySearchRecursive(0, items.length - 1)
    }

    /**
     * Задание №4
     * Реализуйте функцию, которая генерирует список заданной длинны c именами.
     * Функция должна соответствовать всем правилам функционального программирования.
     *
     * Именем является строка, не содержащая иных символов, кроме буквенных, а также начинающаяся с заглавной буквы.
     */

    def generateNames(namesCount: Int): List[String] = {
        if (namesCount < 0) {
            throw new IllegalArgumentException("Invalid namesCount")
        }

        // Генерация случайного имени
        def randomName(): String = {
            val alphabet = ('A' to 'Z') ++ ('a' to 'z')
            val nameLength = Random.nextInt(99)
            val firstLetter = alphabet(Random.nextInt(26))
            val rest = (1 until nameLength).map(_ => alphabet(26 + Random.nextInt(26)))
            (firstLetter +: rest).mkString
        }

        // Генерация списка имен
        List.fill(namesCount)(randomName())
    }

}

/**
 * Задание №5
 *
 * Дана реализация сервиса по смене номера SimpleChangePhoneService с методом changePhone
 * Необходимо написать реализацию этого сервиса с учетом правил работы со сторонними эффектами (SideEffects).
 *
 * Для этого необходимо сначала реализовать собственный сервис работы с телефонными номерами (PhoneServiceSafety),
 * используя при этом методы из unsafePhoneService.
 * Методы должны быть безопасными, поэтому тип возвращаемых значений необходимо определить самостоятельно.
 * Рекомендуется воспользоваться стандартными типами Scala (например Option или Either).
 *
 * Затем, с использованием нового сервиса, необходимо реализовать "безопасную" версию функции changePhone.
 * Функция должна возвращать ok в случае успешного завершения или текст ошибки.
 *
 * Изменять методы внутри SimplePhoneService не разрешается.
 */

object SideEffectExercise {
    import Utils._

    class SimpleChangePhoneService(phoneService: SimplePhoneService) extends ChangePhoneService {
        override def changePhone(oldPhone: String, newPhone: String): String = {
            val oldPhoneRecord = phoneService.findPhoneNumber(oldPhone)
            if (oldPhoneRecord != null) {
                phoneService.deletePhone(oldPhoneRecord)
            }
            phoneService.addPhoneToBase(newPhone)
            "ok"
        }
    }


    class PhoneServiceSafety(unsafePhoneService: SimplePhoneService) {
        def findPhoneNumberSafe(num: String): Option[String] = {
            Option(unsafePhoneService.findPhoneNumber(num))
        }

        def addPhoneToBaseSafe(phone: String): Either[String, String] = {
            try {
                unsafePhoneService.addPhoneToBase(phone)
                Right("ok")
            } catch {
                case e: Throwable => Left(e.getMessage)
            }
        }

        def deletePhoneSafe(phone: String): Either[String, String] = {
            try {
                unsafePhoneService.deletePhone(phone)
                Right("ok")
            } catch {
                case e: Throwable => Left(e.getMessage)
            }
        }
    }

    class ChangePhoneServiceSafe(phoneServiceSafety: PhoneServiceSafety) extends ChangePhoneService {
        override def changePhone(oldPhone: String, newPhone: String): String = {
            phoneServiceSafety.findPhoneNumberSafe(oldPhone) match {
                case Some(_) =>
                    phoneServiceSafety.deletePhoneSafe(oldPhone) match {
                        case Right(_) =>
                            phoneServiceSafety.addPhoneToBaseSafe(newPhone) match {
                                case Right(_) => "ok"
                                case Left(error) => s"Error adding new phone: $error"
                            }
                        case Left(error) =>
                            s"Error deleting old phone: $error"
                    }
                case None => "Old phone number was not found. Cannot change number"
            }
        }
    }
}
