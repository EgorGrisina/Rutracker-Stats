package dto

import java.util.*

data class DTOForumTree(
    val c : HashMap<Int,String>,
    val f : HashMap<Int,String>,
    val tree: HashMap<Int, HashMap<Int,ArrayList<Int>>>
)