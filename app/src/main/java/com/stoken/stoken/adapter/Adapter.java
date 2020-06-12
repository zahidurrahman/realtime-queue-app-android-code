package com.stoken.stoken.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stoken.stoken.R;
import com.stoken.stoken.model.DataModel;

import java.util.List;

public class Adapter extends RecyclerView.Adapter {

    boolean value;
    private List<DataModel> mList;
    private Context ctx;

    public Adapter(Context ctx, List<DataModel> mList) {
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_document, parent, false);
        vh = new CardViewHolderDocument(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CardViewHolderDocument) {
            final DataModel dm = mList.get(position);

            ((CardViewHolderDocument) holder).title.setText(dm.getDocumentTitle());
            ((CardViewHolderDocument) holder).filename.setText("Filename: " + dm.getDocumentFileName());
            ((CardViewHolderDocument) holder).updatedAt.setText("Updated: " + dm.getDocumentUpdatedTime());

            ((CardViewHolderDocument) holder).download.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    // set folder name
                    String folderName = "Documents";

                    if (ctx.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        DownloadManager downloadmanager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(dm.getDocumentLink());

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle(dm.getDocumentTitle());
                        request.setDescription("Downloading " + dm.getDocumentTitle());
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setVisibleInDownloadsUi(false);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, folderName + "/" + dm.getDocumentFileName());
                        downloadmanager.enqueue(request);
                    } else {
                        ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }
            });

        } else {
            if (!value) {
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            } else ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            DataModel object = mList.get(position);
            if (object != null) {
                return object.getType();
            }
        }
        return 0;
    }

    // CardViewHolderDocument
    public class CardViewHolderDocument extends RecyclerView.ViewHolder {
        TextView title, filename, updatedAt;
        Button download;
        View mView;

        CardViewHolderDocument(View v) {
            super(v);
            mView = v;
            title = v.findViewById(R.id.title);
            filename = v.findViewById(R.id.filename);
            updatedAt = v.findViewById(R.id.updatedAt);
            download = v.findViewById(R.id.btnDownload);
        }
    }

    // ProgressBar
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_bar);
        }
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (23 >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = ctx.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(ctx, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

}