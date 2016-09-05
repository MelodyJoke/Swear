package com.teamsolo.swear.foundation.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
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

    private String content;

    private List<String> urls = new ArrayList<>();

    private Map<String, Drawable> caches = new HashMap<>();

    @SuppressWarnings("deprecation")
    private Html.ImageGetter imageGetter = source -> {
        if (!urls.contains(source)) {
            urls.add(source);
            download();

            Drawable drawable = getContext().getResources().getDrawable(R.mipmap.loading_holder_web);
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            int widthEnable = getMeasuredWidth();
            if (width < widthEnable) drawable.setBounds(0, 0, width, height);
            else drawable.setBounds(0, 0, widthEnable, widthEnable * height / width);
            return drawable;
        } else {
            Drawable drawable = caches.get(source);
            if (drawable != null) {
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                int widthEnable = getMeasuredWidth();
                if (width < widthEnable) drawable.setBounds(0, 0, width, height);
                else drawable.setBounds(0, 0, widthEnable, widthEnable * height / width);
                return drawable;
            }
            return getContext().getResources().getDrawable(R.mipmap.loading_failed_web);
        }
    };

    private Html.TagHandler tagHandler = (opening, tag, output, xmlReader) -> {
        // TODO: handle video and audio tag
        if ("video".equals(tag)) {
            if (opening) System.out.println("<");
            System.out.println(output);
            if (!opening) System.out.println(">");
        } else if ("audio".equals(tag)) {
            if (opening) System.out.println("<");
            System.out.println(output);
            if (!opening) System.out.println(">");
        }
    };

    private OnHrefClickListener listener;

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
                    if (listener != null) listener.onClick(link);
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
                LogUtility.i("HtmlTV", "parse url failed: " + url);
            }

            BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    if (bitmap != null) {
                        caches.put(url, new BitmapDrawable(getContext().getResources(), bitmap));
                        LogUtility.i("HtmlTV", "download success: " + url);
                    } else {
                        caches.put(url, getContext().getResources().getDrawable(R.mipmap.loading_failed_web));
                        LogUtility.i("HtmlTV", "download failed: " + url);
                    }

                    getHandler().post(() -> setRichText(content));
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    caches.put(url, getContext().getResources().getDrawable(R.mipmap.loading_failed_web));
                    LogUtility.i("HtmlTV", "download failed: " + url);

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

    public void setOnHrefClickListener(OnHrefClickListener listener) {
        this.listener = listener;
    }

    public interface OnHrefClickListener {
        void onClick(String link);
    }
}
