package Api

import dto.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface ApiScheme {

    @GET("get_tor_status_titles")
    fun getTorStatusTitles() : Call<DTOResponse<HashMap<String, String>>>

    @GET("get_limit")
    fun getLimit() : Call<DTOResponse<DTOLimit>>

    @GET("get_forum_data")
    fun getForumData(@Query("by") by : String = "forum_id", @Query("val") value : String = ""  ) : Call<DTOResponse<HashMap<Long,DTOForum?>>>

    @GET("get_tor_topic_data")
    fun getTopicData(@Query("by") by : String = "topic_id", @Query("val") value : String = ""  ) : Call<DTOResponse<HashMap<Long,DTOTopic?>>>

    @GET("static/cat_forum_tree")
    fun getForumTree() : Call<DTOResponse<DTOForumTree?>>

    @GET("static/pvc/f/{forum_id}")
    fun getTopics(@Path("forum_id") forum_id : Int = 0) : Call<DTOResponse<HashMap<Long, Array<Int>?>?>>

}
