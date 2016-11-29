import kotlinx.nosql.*
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.mongodb.MongoDB

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
        var result = Days.find{name.equal("Monday")}.projection { users }.update(1500)
        println(result)
    }

}