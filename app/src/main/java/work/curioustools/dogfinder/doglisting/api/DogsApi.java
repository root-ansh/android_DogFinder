package work.curioustools.dogfinder.doglisting.api;


import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DogsApi {

    @GET("api/breeds/image/random")
    @NotNull
    Call<DogImageDto> getImage();

    @NotNull public static final String BASE_URL_WITH_SLASH = "https://dog.ceo/";


}
