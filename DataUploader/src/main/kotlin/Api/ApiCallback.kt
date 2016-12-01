package Api

interface ApiCallback<in JsonResultModel> {
    fun onResponse(result: JsonResultModel)
    fun onFailure() {}
}
