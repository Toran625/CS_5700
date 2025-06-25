fun main() {
    val person = Person(null)

    print("Welcome ${person.name ?: "Anonymous User"}")

    var nextJobId = (person.jobId ?: 999) + 1
}

fun doMath(a: Int, b:Int) = a + b