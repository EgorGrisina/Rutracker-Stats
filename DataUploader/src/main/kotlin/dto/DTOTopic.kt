package dto
/**
 * Created by egorg on 30.11.2016.
 */

import java.util.Date

data class DTOTopic(
        var topic_id: Long?,
        var topic_title : String?,
        var forum_name : String?,
        var forum_id : Int?,
        var size : Long?,
        var seeders : Int?,
        var live : Boolean?,
        var date : Date?,
        var reg_time : Long?
)