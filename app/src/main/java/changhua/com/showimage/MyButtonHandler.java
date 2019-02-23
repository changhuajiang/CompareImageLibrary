package changhua.com.showimage;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyButtonHandler {

    private Context mContext;
    private ImageView mImgView;
    private Bitmap returnBitmap = null;

    public final ObservableBoolean isLoading = new ObservableBoolean(false);


    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private final static String IMAGE_URL = "https://cprodctnxsf.att.net/commonLogin/igate_edam/staticContent/attmessages/images/messages1_gfx.png";


    public MyButtonHandler(Context c, ImageView imageView) {
        mContext = c;
        mImgView = imageView;
    }

    public void fetchImage() {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

        isLoading.set(true);

        Glide.with(mContext)
                .load(IMAGE_URL)
                .apply(requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE))
                .apply(requestOptions.skipMemoryCache(true))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        isLoading.set(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        isLoading.set(false);
                        return false;
                    }
                })
                .into(mImgView);
    }


    public void fetchImageWithPicasso() {
        mImgView.setImageResource(android.R.color.transparent);
        isLoading.set(true);
        Picasso.get()
                .load(IMAGE_URL)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mImgView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        isLoading.set(false);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }


    public void fetchRawimage() {

        mImgView.setImageResource(android.R.color.transparent);
        isLoading.set(true);


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(IMAGE_URL)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // Read data on the worker thread
                final byte[] imageBuffer = response.body().bytes();
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                /*
                 * Sets the desired image height and width based on the
                 * ImageView being created.
                 */

                int targetWidth = mImgView.getWidth(); //500;
                int targetHeight = mImgView.getHeight(); //800

                // Before continuing, checks to see that the Thread hasn't
                // been interrupted
                if (Thread.interrupted()) {
                    return;
                }

                /*
                 * Even if the decoder doesn't set a Bitmap, this flag tells
                 * the decoder to return the calculated bounds.
                 */
                bitmapOptions.inJustDecodeBounds = true;

                /*
                 * First pass of decoding to get scaling and sampling
                 * parameters from the image
                 */
                BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, bitmapOptions);

                /*
                 * Sets horizontal and vertical scaling factors so that the
                 * image is expanded or compressed from its actual size to
                 * the size of the target ImageView
                 */
                int hScale = bitmapOptions.outHeight / targetHeight;
                int wScale = bitmapOptions.outWidth / targetWidth;

                /*
                 * Sets the sample size to be larger of the horizontal or
                 * vertical scale factor
                 */
                //
                int sampleSize = Math.max(hScale, wScale);

                /*
                 * If either of the scaling factors is > 1, the image's
                 * actual dimension is larger that the available dimension.
                 * This means that the BitmapFactory must compress the image
                 * by the larger of the scaling factors. Setting
                 * inSampleSize accomplishes this.
                 */
                if (sampleSize > 1) {
                    bitmapOptions.inSampleSize = sampleSize;
                }

                if (Thread.interrupted()) {
                    return;
                }

                // Second pass of decoding. If no bitmap is created, nothing
                // is set in the object.
                bitmapOptions.inJustDecodeBounds = false;


                // Run view-related code back on the main thread
                returnBitmap = BitmapFactory.decodeByteArray(
                        imageBuffer,
                        0,
                        imageBuffer.length,
                        bitmapOptions);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImgView.setImageBitmap(returnBitmap);
                        isLoading.set(false);
                    }
                });
            }
        });

    }


}
