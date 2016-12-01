package dto
/**
 * Created by egorg on 30.11.2016.
 */

import java.util.Date

data class DTOTopic(
        val topic_id: Long?,
        val topic_title : String?,
        val forum_name : String?,
        val forum_id : Int?,
        val size : Long?,
        val seeders : Int?,
        val live : Boolean?,
        val date : Date?,
        val reg_time : Long?
)