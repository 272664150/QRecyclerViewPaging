package com.example.qrecyclerviewpaging;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mWidth;
    private int mHeight;

    public RecyclerViewAdapter(Context context, int width, int height) {
        super();
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public int getItemCount() {
        return PageTypeEnum.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return PageTypeEnum.values()[position].getValue();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PageTypeEnum.NATIVE.getValue()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_native, null);
            return new NativeViewHolder(view);
        } else if (viewType == PageTypeEnum.H5.getValue()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_h5, null);
            return new H5ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setMinimumWidth(mWidth);
        if (getItemViewType(position) == PageTypeEnum.NATIVE.getValue()) {
            holder.itemView.setMinimumHeight(mHeight);
            NativeViewHolder nativeViewHolder = (NativeViewHolder) holder;
            nativeViewHolder.mTextView.setText("Native");
        } else if (getItemViewType(position) == PageTypeEnum.H5.getValue()) {
            holder.itemView.setMinimumHeight(mHeight * 2);
            H5ViewHolder h5ViewHolder = (H5ViewHolder) holder;
            h5ViewHolder.mWebView.loadUrl("https://m.cnbeta.com");
        }
    }

    private class NativeViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public NativeViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.view_native);
        }
    }

    private class H5ViewHolder extends RecyclerView.ViewHolder {
        private WebView mWebView;

        public H5ViewHolder(View view) {
            super(view);
            mWebView = view.findViewById(R.id.view_h5);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadsImagesAutomatically(false);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
    }
}
