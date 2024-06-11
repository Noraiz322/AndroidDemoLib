import com.growcredit.widget.plugin.api.ApiService
import com.growcredit.widget.plugin.api.RetrofitHelper

object ApiServiceFactory {
    val apiService: ApiService by lazy {
        RetrofitHelper.getInstance().create(ApiService::class.java)
    }
}