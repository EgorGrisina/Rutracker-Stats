import org.litote.kmongo.*
import org.litote.kmongo.util.KMongoUtil.toBson
import java.util.*

val HOST = "egritsina.domain.corp"
val PORT = 27017
val DB_NAME = "Rutracker"
val COLLECTION_TOPICS = "topics"

//data class Day(val _id: String?, val name: String, val weekend: Boolean, var users : Long)

fun main(args: Array<String>) {
    println("Hello kotlin!")
    /*val mongoURI = MongoClientURI("mongodb://egritsina.domain.corp:27017/test")
    val db = MongoDB(mongoURI, schemas = arrayOf(Days))

    db.withSession {
        var result = Days.find{name.equal("Monday")}.projection { users }.update(1500)
        println(result)
    }*/
    val client = KMongo.createClient(HOST, PORT)
    val database = client.getDatabase(DB_NAME) //normal java driver usage
    val collection = database.getCollection<DTOTopic>(COLLECTION_TOPICS) //KMongo extension method

    /*val topics = ArrayList<DTOTopic>()
    val random = Random()

    for (i in 0..200) {
        val forum_id = random.nextInt(10)
        val seeds = random.nextInt(100)
        val topic = DTOTopic("Фильм $i", "Форум $forum_id", forum_id, size = random.nextInt(10000000).toLong(), seeders = seeds,  live = seeds>20 )
        topics.add(topic)
    }


    collection.insertMany(topics)
*/


    println("Result: ")
    val result = collection.find()
    for (topic in result) {
        println(topic)
    }


    /*val newday = result.toList().get(0)
    newday.users = 3000
    println(collection.updateOne("{name: '${newday.name}'}", "{\$set: {users : ${newday.users}} }"))*/

}