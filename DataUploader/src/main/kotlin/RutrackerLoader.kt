import Api.*
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

    var current_cat_number = -1
    var current_parent_id = -1
    var current_forum_number = -1

    fun start() {
        limit = 100
        current_cat_number = -1
        current_parent_id = -1
        current_forum_number = -1

        val date = Calendar.getInstance()
        println("Start parsing at " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":" + date.get(Calendar.SECOND))
        startTime = date.timeInMillis

        startInit()
    }

    private fun parseCategory(cat_id : Int) : Boolean {
        val categoryTree = forumTree.tree.get(cat_id)
        if (categoryTree?.size!! > 0) {
            println("Start parsing category: " + cat_id+" : "+forumTree.c.get(cat_id))
            current_forum_number++
            current_parent_id = categoryTree?.keys?.elementAt(current_forum_number)!!
            //TODO
            //doParsing()
        } else {
            println("No forums in category: " + cat_id+" : "+forumTree.c.get(cat_id))
            doParsing()
        }
        return true
    }

    private fun doParsing() {
        if (current_cat_number < forumTree.c.size-1) {
            current_cat_number++
            current_forum_number = -1
            current_parent_id = -1
            parseCategory(forumTree.c.keys.elementAt(current_cat_number))
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