package work.curioustools.dog_network;

import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class GlideUtil {
    public static void loadImageFromInternet(String url, ImageView imv) {
        Glide.with(imv.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imv);

    }

}
