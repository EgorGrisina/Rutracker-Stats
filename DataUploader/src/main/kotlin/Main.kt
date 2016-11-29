import kotlinx.nosql.boolean
import kotlinx.nosql.equal
import kotlinx.nosql.integer
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDB
import kotlinx.nosql.string

/**
 * Created by EGritsina on 28.11.2016.
 */

object Users : DocumentSchema<User>("users", User::class) {
    val id = integer("id")
    val name = string("name")
}

object Days : DocumentSchema<Day>("days", Day::class) {
    val name = string("name")
    val weekend = boolean("weekend")
    val users = integer("users")
}

data class Day(val name: String, val weekend: Boolean, val users : Int)
data class User(val id : Int, val name: String)

fun main(args: Array<String>) {
    println("Hello kotlin!")
    val db = MongoDB(database = "test", schemas = arrayOf(Days))

    db.withSession {
        var result = Days.insert(Day("Monday", false, 1000))
        println(result)
        result = Days.insert(Day("Tuesday", false, 1200))
        println(result)
        result = Days.insert(Day("Wednesday", false, 900))
        println(result)
        result = Days.insert(Day("Thursday", false, 1250))
        println(result)
        result = Days.insert(Day("Friday", false, 1300))
        println(result)
        result = Days.insert(Day("Saturday", false, 1400))
        println(result)
        result = Days.insert(Day("Sunday", false, 1550))
        println(result)
    }

}