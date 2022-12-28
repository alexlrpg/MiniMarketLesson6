package api;

import dto.GetCategoryResponce;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryService {

    @GET("categories/{id}")
    Call<GetCategoryResponce> getCategory(@Path("id") int id);
}
