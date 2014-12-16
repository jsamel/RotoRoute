package com.example.flyboyz.rotoroute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

/**
 * Created by joelsamelson on 12/3/14.
 */
public class GifMovieView extends View {
    private Movie mMovie;
    private long movieStart = System.currentTimeMillis();

    public GifMovieView(Context context) {
        super(context);
        initializeView();
    }

    public GifMovieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public GifMovieView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView();
    }

    private void initializeView() {
        //R.drawable.loader - our animated GIF
        //InputStream is = getContext().getResources().openRawResource(R.drawable.loader);
        //mMovie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
            int relTime = (int) ((now - movieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() - mMovie.width(), getHeight() - mMovie.height());
            this.invalidate();
        }
    }
}
