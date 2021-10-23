package work.curioustools.dogfinder.doglisting.api

import com.google.gson.annotations.SerializedName

data class DogImageDto(
    @SerializedName("message") val url:String? = null,
    @SerializedName("status") val status:String? = null
)