package ch.patland.loopyloop.utils

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class MyGlideApp: AppGlideModule() {
    /*
        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
            int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
            builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes));
            builder.setDefaultRequestOptions(requestOptions(context));
            builder.build(context);
        }

        @Override
        public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();

            OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);

            glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
        }

        private static RequestOptions requestOptions(Context context){
        return new RequestOptions()
                .signature(new ObjectKey(
                        System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
                .override(200, 200)
                .centerCrop()
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .encodeQuality(100)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .format(PREFER_ARGB_8888)
                .skipMemoryCache(false);
    }
     */
}