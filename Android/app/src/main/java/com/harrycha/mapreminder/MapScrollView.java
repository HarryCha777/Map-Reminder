// Source: https://stackoverflow.com/questions/30525066/how-to-set-google-map-fragment-inside-scroll-view

package com.harrycha.mapreminder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MapScrollView extends ScrollView {

    public MapScrollView(Context context) {
        super(context);
    }

    public MapScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //Log.i("CustomScrollView", "onInterceptTouchEvent: DOWN super false" );
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                return false; // redirect MotionEvents to ourself

            case MotionEvent.ACTION_CANCEL:
                // Log.i("CustomScrollView", "onInterceptTouchEvent: CANCEL super false" );
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_UP:
                //Log.i("CustomScrollView", "onInterceptTouchEvent: UP super false" );
                return false;

            default:
                //Log.i("CustomScrollView", "onInterceptTouchEvent: " + action );
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        //Log.i("CustomScrollView", "onTouchEvent. action: " + ev.getAction() );
        return true;
    }
}
