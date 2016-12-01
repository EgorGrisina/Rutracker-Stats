import Api.*
import com.mongodb.client.MongoCollection
import dto.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by egorg on 30.11.2016.
 */

class RutrackerLoader {

    var limit = 100
    val apiScheme = Api.scheme
    lateinit var forumTree : DTOForumTree
    var startTime : Long = 0
    val DELAY : Long = 250 // 0.25 sec

    var current_forum_number = -1

    lateinit var DBCollection : MongoCollection<DTOTopic>

    fun start(collection : MongoCollection<DTOTopic>) {
        DBCollection = collection
        limit = 100
        current_forum_number = -1

        val date = Calendar.getInstance()
        println("Start parsing at " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND))
        startTime = date.timeInMillis

        startInit()
    }

    private fun getParentForumId(forum_id: Int) : Int {
        for (forumsMap in forumTree.tree.values) {
            for (forum in forumsMap.keys) {
                if (forum == forum_id) {
                    return forum_id
                } else {
                    for (subforum in forumsMap.get(forum)!!) {
                        if (subforum == forum_id) {
                            return forum
                        }
                    }
                }
            }
        }
        return  -1
    }

    private fun saveTopicsToMongo(topicsList : MutableList<DTOTopic?>?, parent_id : Int) {
        if (topicsList == null || topicsList.size == 0) {
            return
        }

        Thread(Runnable {
            val parsedTopics = ArrayList<DTOTopic>()
            for (topic in topicsList) {
                if (topic != null) {
                    try {
                        topic.forum_id = parent_id
                        topic.forum_name = forumTree.f.get(parent_id)
                        topic.size = topic.size!! / (1024 * 1024)
                        topic.live = !(topic.seeders!! < 2 && (System.currentTimeMillis() - topic.reg_time!!) > TimeUnit.DAYS.toMillis(5))
                        topic.date = Date()

                        parsedTopics.add(topic)
                    } catch (e : Exception) {
                        println("Parse error =( " + e.message)
                    }
                }
            }
            if (parsedTopics.size > 0) {
                val result = DBCollection.insertMany(parsedTopics)
                println("Insert to database " + parsedTopics.size + " topics")
                println(result)
            }
        }).start()
    }

    private fun parseTopicsList(topicsList : MutableList<Long>, parent_id : Int, forum_id : Int) {
        println("forum : "+ forum_id + " parrent : " + parent_id+ " topics: " + topicsList)
        if (topicsList.size > 0) {
            val topicsToParse = topicsList.subList(0, if (limit <= topicsList.size) limit - 1 else topicsList.size)
            val newTopicsList = topicsList.subList(if (limit <= topicsList.size) limit - 1 else topicsList.size, topicsList.size)
            if (topicsToParse.size > 0) {
                var value : String = topicsToParse.get(0).toString()
                for(i in 1..topicsToParse.size-1) {
                    value += ","+topicsToParse.get(i)
                }
                enqueue(apiScheme.getTopicData(value = value), object : ApiCallback<HashMap<Long, DTOTopic?>> {
                    override fun onResponse(result: HashMap<Long, DTOTopic?>) {
                        if (result != null) {
                            for (id in result.keys) {
                                val topic = result.get(id)
                                if (topic!=null) {
                                    topic.topic_id = id
                                }
                            }
                            saveTopicsToMongo(result.values.toMutableList(), parent_id)
                        }
                        Thread.sleep(DELAY)
                        parseTopicsList(newTopicsList, parent_id, forum_id)
                    }

                    override fun onFailure() {
                        super.onFailure()
                        println("parseTopicsList: " + "onFail")

                        Thread.sleep(DELAY)
                        parseTopicsList(newTopicsList, parent_id, forum_id)
                    }

                })
            }

        } else {
            doParsing()
        }
    }

    private fun parseForum(forum_id : Int) {
        println("Parse forum: " + forum_id+" : " + forumTree.f.get(forum_id))
        enqueue(apiScheme.getTopics(forum_id), object : ApiCallback<HashMap<Long, Array<Int>?>?> {
            override fun onResponse(result: HashMap<Long, Array<Int>?>?) {
                if (result != null) {
                    parseTopicsList(result.keys.toMutableList(), getParentForumId(forum_id), forum_id)
                } else {
                    println("Forum: " + forum_id+" : " + "No topics, continue")
                    doParsing()
                }
            }

            override fun onFailure() {
                super.onFailure()
                println("Forum: " + forum_id+" : " + "No topics, continue")
                doParsing()
            }

        })

    }

    private fun doParsing() {
        if (current_forum_number < forumTree.f.size-1) {
            current_forum_number ++
            parseForum(forumTree.f.keys.elementAt(current_forum_number))
        } else {
            val date = Calendar.getInstance()
            println("End parsing at " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND))
            if (startTime > 0) {
                println("duration: " + TimeUnit.MILLISECONDS.toMinutes(date.timeInMillis - startTime)+" minutes")
            }
        }
    }

    private fun setForumTree() {
        enqueue(apiScheme.getForumTree(), object : ApiCallback<DTOForumTree?> {
            override fun onResponse(result: DTOForumTree?) {
                if (result != null) {
                    forumTree = result
                    doParsing()
                } else {
                    println("Forum tree is Null!! Exit")
                }
            }

            override fun onFailure() {
                super.onFailure()
                println("Fail to get forum tree !! Exit")
            }
        })
    }

    private fun startInit( ){
        enqueue(apiScheme.getLimit(), object : ApiCallback<DTOLimit> {
            override fun onResponse(result: DTOLimit) {
                limit = result.limit
                if (limit > 0) {
                    setForumTree()
                } else {
                    println("Limit is 0 =( Exit")
                }
            }

            override fun onFailure() {
                super.onFailure()
                println("Fail to get limit !! Exit")
            }
        })
    }

    private fun test() {
        enqueue(apiScheme.getTopics(1), object : ApiCallback<HashMap<Long, Array<Int>?>?> {
            override fun onResponse(result: HashMap<Long, Array<Int>?>?) {
                println(result)
            }

        })
    }

    inner class DefaultCallback<JsonResultModel>(val apiCallback: ApiCallback<JsonResultModel>) : Callback<DTOResponse<JsonResultModel>> {
        override fun onFailure(call: Call<DTOResponse<JsonResultModel>>?, t: Throwable?) {
            if (t is SocketException || t is SocketTimeoutException) {
                println("ON FAIL : trouble with internet connection")
            } else {
                println("ON FAIL : " + t)
            }
            println("wait 2 minutes")
            Thread.sleep(TimeUnit.MINUTES.toMillis(2))
            enqueue(call, apiCallback)
        }

        override fun onResponse(call: Call<DTOResponse<JsonResultModel>>?, response: Response<DTOResponse<JsonResultModel>>?) {
            response?.let {
                val _body : DTOResponse<JsonResultModel>? = response.body()
                _body?.let {
                    if (_body.result != null) {
                        apiCallback.onResponse(_body.result)
                    }
                }
                val _errorBody : ResponseBody? = response?.errorBody();
                _errorBody?.let {
                    println("ON FAIL : " + _errorBody.string())
                    apiCallback.onFailure()
                }
            }
        }

    }

    fun <JsonResultModel> enqueue(call: Call<DTOResponse<JsonResultModel>>?, callback: ApiCallback<JsonResultModel>) {
        call?.enqueue(DefaultCallback(callback))
    }

}