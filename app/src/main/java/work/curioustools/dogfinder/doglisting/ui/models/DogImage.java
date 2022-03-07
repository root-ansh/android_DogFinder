package work.curioustools.dogfinder.doglisting.ui.models;

import org.jetbrains.annotations.NotNull;


public final class DogImage {
    @NotNull private final int requestCount;

    @NotNull private final String image;

    @NotNull public final int getRequestCount() {
        return this.requestCount;
    }

    @NotNull public final String getImage() {
        return this.image;
    }

    public DogImage(int requestCount, @NotNull String image) {
        this.requestCount = requestCount;
        this.image = image;
    }


    @NotNull
    public final DogImage copy(int requestCount, @NotNull String image) {
        return new DogImage(requestCount, image);
    }

    @NotNull
    public String toString() {
        return "DogImage(requestCount=" + this.requestCount + ", image=" + this.image + ")";
    }
}
