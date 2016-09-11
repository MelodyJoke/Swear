package com.teamsolo.swear.foundation.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.base.util.LogUtility;
import com.teamsolo.swear.R;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.imagepipeline.request.ImageRequest.fromUri;

/**
 * description: textView to support html rich text
 * author: Melody
 * date: 2016/9/5
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class HtmlSupportTextView extends TextView {

    private static final String TAG = "HtmlTv";

    private String content;

    private List<String> urls = new ArrayList<>();

    private Map<String, Drawable> caches = new HashMap<>();

    @SuppressWarnings("deprecation")
    private Html.ImageGetter imageGetter = source -> {
        if (!urls.contains(source)) {
            urls.add(source);
            download();

            Drawable drawable = getContext().getResources().getDrawable(R.mipmap.loading_holder_web);
            fixDrawableSize(drawable);
            return drawable;
        } else {
            Drawable drawable = caches.get(source);

            if (drawable != null) {
                fixDrawableSize(drawable);

                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null && !bitmap.isRecycled()) return drawable;
                } else return drawable;
            }

            Drawable drawableError = getContext().getResources().getDrawable(R.mipmap.loading_failed_web);
            fixDrawableSize(drawableError);
            return drawableError;
        }
    };

    @SuppressWarnings("deprecation")
    private Html.TagHandler tagHandler = (opening, tag, output, xmlReader) -> {
        if (!opening) return;

        try {
            if ("video".equals(tag.toLowerCase())) {
                final String link = getAttrs(xmlReader).get("src");

                Drawable drawable = getContext().getResources().getDrawable(R.mipmap.web_cover_video);
                fixDrawableSize(drawable);
                ClickableImageSpan imageSpan = new ClickableImageSpan(drawable) {
                    @Override
                    public void onClick(View view) {
                        if (onVideoClickListener != null) onVideoClickListener.onClick(view, link);
                    }
                };
                output.setSpan(imageSpan, output.length() - 1, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("audio".equals(tag.toLowerCase())) {
                final String link = getAttrs(xmlReader).get("src");

                Drawable drawable = getContext().getResources().getDrawable(R.mipmap.web_cover_audio);
                fixDrawableSize(drawable);
                ClickableImageSpan imageSpan = new ClickableImageSpan(drawable) {
                    @Override
                    public void onClick(View view) {
                        if (onAudioClickListener != null) onAudioClickListener.onClick(view, link);
                    }
                };
                output.setSpan(imageSpan, output.length() - 1, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            LogUtility.e(TAG, e.getMessage());
        }
    };

    private onClickListener onHrefClickListener, onVideoClickListener, onAudioClickListener;

    public HtmlSupportTextView(Context context) {
        super(context);
    }

    public HtmlSupportTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlSupportTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HtmlSupportTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("deprecation")
    public void setRichText(String content) {
        this.content = content;

        CharSequence charSequence = Html.fromHtml(content, imageGetter, tagHandler);
        SpannableStringBuilder builder = new SpannableStringBuilder(charSequence);
        URLSpan[] urlSpans = builder.getSpans(0, charSequence.length(), URLSpan.class);
        for (URLSpan span : urlSpans) {
            int start = builder.getSpanStart(span);
            int end = builder.getSpanEnd(span);
            int flag = builder.getSpanFlags(span);
            final String link = span.getURL();
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (onHrefClickListener != null) onHrefClickListener.onClick(widget, link);
                }
            }, start, end, flag);
            builder.removeSpan(span);
        }

        setText(builder);
        invalidate();
    }

    @SuppressWarnings("deprecation")
    private void download() {
        for (String temp :
                urls) {
            final String url = temp;
            if (TextUtils.isEmpty(url)) continue;

            ImageRequest request;
            try {
                request = ImageRequest.fromUri(Uri.parse(url));
            } catch (Exception e) {
                request = fromUri(DisplayUtility.getResourceUri(R.mipmap.loading_failed_web, getContext().getPackageName()));
                LogUtility.i(TAG, "parse url failed: " + url);
            }

            final ImageRequest copy = request;
            BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        caches.put(url, new BitmapDrawable(getContext().getResources(), bitmap.copy(bitmap.getConfig(), false)));
                        LogUtility.i(TAG, "download success: " + url);
                    } else {
                        caches.put(url, getContext().getResources().getDrawable(R.mipmap.loading_failed_web));
                        LogUtility.i(TAG, "download failed: " + url);
                    }

                    getHandler().post(() -> setRichText(content));
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    caches.put(url, getContext().getResources().getDrawable(R.mipmap.loading_failed_web));
                    LogUtility.i(TAG, "download failed: " + url);

                    getHandler().post(() -> setRichText(content));
                }
            };

            if (Fresco.getImagePipeline().isInBitmapMemoryCache(request.getSourceUri()))
                Fresco.getImagePipeline()
                        .fetchImageFromBitmapCache(request, getContext())
                        .subscribe(subscriber, CallerThreadExecutor.getInstance());
            else
                Fresco.getImagePipeline()
                        .fetchDecodedImage(request, getContext())
                        .subscribe(subscriber, CallerThreadExecutor.getInstance());
        }
    }

    public List<String> getPictureUrls() {
        return urls;
    }

    public void setOnHrefClickListener(onClickListener listener) {
        this.onHrefClickListener = listener;
    }

    public void setOnVideoClickListener(onClickListener listener) {
        this.onVideoClickListener = listener;
    }

    public void setOnAudioClickListener(onClickListener listener) {
        this.onAudioClickListener = listener;
    }

    private Map<String, String> getAttrs(XMLReader reader) throws Exception {
        Field elementField = reader.getClass().getDeclaredField("theNewElement");
        elementField.setAccessible(true);
        Object element = elementField.get(reader);
        Field attrsField = element.getClass().getDeclaredField("theAtts");
        attrsField.setAccessible(true);
        Object attrs = attrsField.get(element);
        Field dataField = attrs.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        String[] data = (String[]) dataField.get(attrs);
        Field lengthField = attrs.getClass().getDeclaredField("length");
        lengthField.setAccessible(true);
        int len = (Integer) lengthField.get(attrs);

        HashMap<String, String> attributes = new HashMap<>();

        for (int i = 0; i < len; i++)
            attributes.put(data[i * 5 + 1], data[i * 5 + 4]);

        return attributes;
    }

    private void fixDrawableSize(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int widthEnable = getMeasuredWidth();
        if (width < widthEnable) drawable.setBounds(0, 0, width, height);
        else drawable.setBounds(0, 0, widthEnable, widthEnable * height / width);
    }

    public interface onClickListener {
        void onClick(View v, String link);
    }

    public void recycle() {
        for (Map.Entry<String, Drawable> entry
                : caches.entrySet()) {
            Drawable drawable = entry.getValue();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
            }
        }

        caches.clear();
        caches = null;
    }
}
