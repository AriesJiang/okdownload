package com.liulishuo.okdownload.sample;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.okdownload.DownloadMonitor;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

/**
 * Created by JiangYiDong on 2018/12/29.
 */
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("FileDownloadMonitor", "SampleApplication onCreate ");

//        FileDownloadMonitor.setGlobalMonitor(new FileDownloadMonitor.IMonitor() {
//            @Override
//            public void onRequestStart(int count, boolean serial, FileDownloadListener lis) {
//                Log.d("FileDownloadMonitor", "onRequestStart count=" + count + " serial=" + serial);
//            }
//
//            @Override
//            public void onRequestStart(BaseDownloadTask task) {
//                Log.d("FileDownloadMonitor", "onRequestStart task=" + task.getFilename());
//
//            }
//
//            @Override
//            public void onTaskBegin(BaseDownloadTask task) {
//                Log.d("FileDownloadMonitor", "onTaskBegin task=" + task.getFilename());
//
//            }
//
//            @Override
//            public void onTaskStarted(BaseDownloadTask task) {
//                Log.d("FileDownloadMonitor", "onTaskStarted task=" + task.getFilename());
//
//            }
//
//            @Override
//            public void onTaskOver(BaseDownloadTask task) {
//                Log.d("FileDownloadMonitor", "onTaskOver task=" + task.getFilename());
//
//            }
//        });
//        FileDownloader.init(this);

        OkDownload.with().setMonitor(new DownloadMonitor() {
            @Override
            public void taskStart(DownloadTask task) {
                Log.d("DownloadMonitor", "taskStart getFilename=" + task.getFilename() + " task.toString=" + task.toString());
            }

            @Override
            public void taskDownloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
                Log.d("DownloadMonitor", "taskDownloadFromBreakpoint getFilename=" + task.getFilename() + " info=" + info.toString());
            }

            @Override
            public void taskDownloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @Nullable ResumeFailedCause cause) {
                Log.d("DownloadMonitor", "taskDownloadFromBeginning getFilename=" + task.getFilename() + " info=" + info.toString() + " cause=" + cause.toString());
            }

            @Override
            public void taskEnd(DownloadTask task, EndCause cause, @Nullable Exception realCause) {
                Log.d("DownloadMonitor", "taskEnd getFilename=" + task.getFilename() + " cause=" + cause + " realCause=" + realCause);
            }
        });
    }
}
