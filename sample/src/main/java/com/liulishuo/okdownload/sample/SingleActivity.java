/*
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liulishuo.okdownload.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.okdownload.*;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.liulishuo.okdownload.sample.base.BaseSampleActivity;
import com.liulishuo.okdownload.sample.util.DemoUtil;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * On this demo you can see the simplest way to download a task.
 */
public class SingleActivity extends BaseSampleActivity {

    private static final String TAG = "SingleActivity";
    private DownloadTask task;
    private DownloadTask task2;
    private DownloadTask taskOld;
    private UnifiedListenerManager manager;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        manager = new UnifiedListenerManager();
        initSingleDownload(
                (TextView) findViewById(R.id.statusTv),
                (ProgressBar) findViewById(R.id.progressBar),
                findViewById(R.id.actionView),
                (TextView) findViewById(R.id.actionTv));
        initSingleDownload2(
                (TextView) findViewById(R.id.statusTv2),
                (ProgressBar) findViewById(R.id.progressBar2),
                findViewById(R.id.actionView2),
                (TextView) findViewById(R.id.actionTv2));

        testTaskState();
    }

    @Override public int titleRes() {
        return R.string.single_download_title;
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (task != null) task.cancel();
    }

    private void initSingleDownload(TextView statusTv, ProgressBar progressBar, View actionView,
                                    TextView actionTv) {
        initTask();
        initStatus(task, statusTv, progressBar);
        initAction(task, actionView, actionTv, statusTv, progressBar);
    }

    private void initSingleDownload2(TextView statusTv, ProgressBar progressBar, View actionView,
                                    TextView actionTv) {
        initTask2();
        initStatus(task2, statusTv, progressBar);
        initAction(task2, actionView, actionTv, statusTv, progressBar);
    }

    private void initTask() {
        final String filename = "single-test";
        final String url =
                "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        final File parentFile = DemoUtil.getParentFile(this);
        task = new DownloadTask.Builder(url, parentFile)
                .setFilename(filename)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(100)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
        task.setTag("task1");
    }

    private void initTask2() {
        final String filename = "single-test";
        final String url =
                "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        final File parentFile = DemoUtil.getParentFile(this);
        task2 = new DownloadTask.Builder(url, parentFile)
                .setFilename(filename)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(100)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(true)
                .build();
        task2.setTag("task2");
    }

    private void testTaskState() {
        final String filename = "single-test";
        final String url =
                "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        final File parentFile = DemoUtil.getParentFile(this);
        DownloadTask test = new DownloadTask.Builder(url, parentFile)
                .setFilename(filename)
                .build();
        final StatusUtil.Status status = StatusUtil.getStatus(test);
        Log.d(TAG, "test status: " + status);
    }

    private void initStatus(DownloadTask task, TextView statusTv, ProgressBar progressBar) {
        final StatusUtil.Status status = StatusUtil.getStatus(task);
        if (status == StatusUtil.Status.COMPLETED) {
            progressBar.setProgress(progressBar.getMax());
        }

        statusTv.setText(status.toString());
        final BreakpointInfo info = StatusUtil.getCurrentInfo(task);
        if (info != null) {
            Log.d(TAG, "init status with: " + info.toString());

            DemoUtil.calcProgressToView(progressBar, info.getTotalOffset(), info.getTotalLength());
        }
    }

    private void initAction(final DownloadTask task, final View actionView, final TextView actionTv, final TextView statusTv,
                            final ProgressBar progressBar) {
        actionTv.setText(R.string.start);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startTask1(task, statusTv, progressBar, actionTv);
//                final boolean started = task.getTag() != null;
//                if (started) {
//                    // to cancel
//                    task.cancel();
//                } else {
//                    actionTv.setText(R.string.cancel);
//                    // to start
//                    startTask(task, statusTv, progressBar, actionTv);
////                    startTask3(task, statusTv, progressBar, actionTv);
//                    // mark
//                    task.setTag("mark-task-started");
//                }

            }
        });
    }

    private void startTask2(DownloadTask task, final TextView statusTv, final ProgressBar progressBar,
                            final TextView actionTv) {
        DownloadListener2 downloadListener2 = new DownloadListener2() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @android.support.annotation.Nullable
                    Exception realCause) {

            }
        };
    }

    private void startTask1(DownloadTask task, final TextView statusTv, final ProgressBar progressBar,
                            final TextView actionTv) {
        DownloadListener1 downloadListener1 = new DownloadListener1() {
            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
                Log.e(TAG, "task=" + task.getTag() + "  task is started");
            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
                Log.e(TAG, "task=" + task.getTag() + "  task is retry");
            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
                Log.e(TAG, "task=" + task.getTag() + "  task is connected");
                final String status = "Connect End " + blockCount;
                statusTv.setText(status);
            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                DemoUtil.calcProgressToView(progressBar, currentOffset, totalLength);
                Log.d(TAG, "progressBar.getMax()=" + progressBar.getMax()
                        + "  downloadListener1= "
                        + this
                        + "  task="
                        + task.getTag()
                        + "  task is progress="
                        + ((float) currentOffset / totalLength) * progressBar.getMax()
                        + "  totalLength=" + totalLength);
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @android.support.annotation.Nullable
                    Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                Log.e(TAG, "task=" + task.getTag() + "  file is taskEnd  cause=" + cause + " realCause=" + realCause);
                final String statusWithSpeed = cause.toString();
                statusTv.setText(statusWithSpeed);

                actionTv.setText(R.string.start);
                // mark
                task.setTag(null);
                if (cause == EndCause.COMPLETED) {
                    final String realMd5 = fileToMD5(task.getFile().getAbsolutePath());
                    if (!realMd5.equalsIgnoreCase("f836a37a5eee5dec0611ce15a76e8fd5")) {
                        Log.e(TAG, "file is wrong because of md5 is wrong " + realMd5);
                    }
                }
            }
        };
//        manager.attachAndEnqueueIfNotRun(task, downloadListener1);
        manager.enqueueTaskWithUnifiedListener(task, downloadListener1);

    }

    private void startTask3(DownloadTask task, final TextView statusTv, final ProgressBar progressBar,
                            final TextView actionTv) {
        DownloadListener3 downloadListener3 = new DownloadListener3() {

            @Override
            protected void started(@NonNull DownloadTask task) {
                Log.e(TAG, "task is started");

            }

            @Override
            protected void completed(@NonNull DownloadTask task) {
                Log.e(TAG, "task is completed");
                statusTv.setText("completed");

                actionTv.setText(R.string.start);
                // mark
                task.setTag(null);
                final String realMd5 = fileToMD5(task.getFile().getAbsolutePath());
                if (!realMd5.equalsIgnoreCase("f836a37a5eee5dec0611ce15a76e8fd5")) {
                    Log.e(TAG, "file is wrong because of md5 is wrong " + realMd5);
                }
            }

            @Override
            protected void canceled(@NonNull DownloadTask task) {
                Log.e(TAG, "task is canceled");
                // mark
                task.setTag(null);
            }

            @Override
            protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
                Log.e(TAG, "task is error:" + e.toString());
                // mark
                task.setTag(null);
            }

            @Override
            protected void warn(@NonNull DownloadTask task) {
                Log.e(TAG, "task is warn");
                // mark
                task.setTag(null);
            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
                Log.e(TAG, "task is retry");
            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
                Log.e(TAG, "task is connected");
                final String status = "Connect End " + blockCount;
                statusTv.setText(status);
            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                DemoUtil.calcProgressToView(progressBar, currentOffset, totalLength);
                Log.d(TAG, "task is progress=" + ((float) currentOffset / totalLength) * progressBar.getMax());
            }
        };
        manager.attachAndEnqueueIfNotRun(task, downloadListener3);

    }

    private void startTask(DownloadTask task, final TextView statusTv, final ProgressBar progressBar,
                           final TextView actionTv) {


        DownloadListener4WithSpeed listener4WithSpeed = new DownloadListener4WithSpeed() {
            private long totalLength;
            private String readableTotalLength;

            @Override public void taskStart(@NonNull DownloadTask task) {
                Log.d(TAG, "file is taskStart");
                statusTv.setText(R.string.task_start);
            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                                  boolean fromBreakpoint,
                                  @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
                statusTv.setText(R.string.info_ready);

                totalLength = info.getTotalLength();
                readableTotalLength = Util.humanReadableBytes(totalLength, true);
                DemoUtil.calcProgressToView(progressBar, info.getTotalOffset(), totalLength);
            }

            @Override public void connectStart(@NonNull DownloadTask task, int blockIndex,
                                               @NonNull Map<String, List<String>> requestHeaders) {
                final String status = "Connect Start " + blockIndex;
                statusTv.setText(status);
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode,
                                   @NonNull Map<String, List<String>> responseHeaders) {
                final String status = "Connect End " + blockIndex;
                statusTv.setText(status);
            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex,
                                      long currentBlockOffset,
                                      @NonNull SpeedCalculator blockSpeed) {
                final String readableOffset = Util.humanReadableBytes(currentBlockOffset, true);
                final String progressStatus = readableOffset + "/" + readableTotalLength;
                final String speed = blockSpeed.speed();
                final String progressStatusWithSpeed = progressStatus + "(" + speed + ")";
                Log.d(TAG, "progressBlock  blockIndex=" + blockIndex + " progressStatusWithSpeed=" + progressStatusWithSpeed);
            }

            @Override public void progress(@NonNull DownloadTask task, long currentOffset,
                                           @NonNull SpeedCalculator taskSpeed) {
                final String readableOffset = Util.humanReadableBytes(currentOffset, true);
                final String progressStatus = readableOffset + "/" + readableTotalLength;
                final String speed = taskSpeed.speed();
                final String progressStatusWithSpeed = progressStatus + "(" + speed + ")";


                statusTv.setText(progressStatusWithSpeed);
                DemoUtil.calcProgressToView(progressBar, currentOffset, totalLength);
                Log.d(TAG, "task=" + task.getTag() + " file is progress=" + ((float) currentOffset / totalLength) * progressBar.getMax());
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info,
                                 @NonNull SpeedCalculator blockSpeed) {
                Log.d(TAG, "file is blockEnd");
            }

            @Override public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause,
                                          @Nullable Exception realCause,
                                          @NonNull SpeedCalculator taskSpeed) {
                Log.d(TAG, "file is taskEnd  cause=" + cause + " realCause=" + realCause);
                final String statusWithSpeed = cause.toString() + " " + taskSpeed.averageSpeed();
                statusTv.setText(statusWithSpeed);

                actionTv.setText(R.string.start);
                // mark
                task.setTag(null);
                if (cause == EndCause.COMPLETED) {
                    final String realMd5 = fileToMD5(task.getFile().getAbsolutePath());
                    if (!realMd5.equalsIgnoreCase("f836a37a5eee5dec0611ce15a76e8fd5")) {
                        Log.e(TAG, "file is wrong because of md5 is wrong " + realMd5);
                    }
                }
            }
        };

//        manager.enqueueTaskWithUnifiedListener(task, listener4WithSpeed);
        manager.attachAndEnqueueIfNotRun(task, listener4WithSpeed);
//        if (taskOld != null) {
//            manager.enqueueTaskWithUnifiedListener(taskOld, listener4WithSpeed);
//        } else {
//            taskOld = task;
//            task.enqueue(listener4WithSpeed);
//            task.setTag("mark-task-started");
//        }
    }

    @SuppressFBWarnings(value = "REC")
    public static String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0) {
                    digest.update(buffer, 0, numRead);
                }
            }
            byte[] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception ignored) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "file to md5 failed", e);
                }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            buf.append(Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buf.toString().toUpperCase();
    }


    private boolean isTaskRunning() {
        final StatusUtil.Status status = StatusUtil.getStatus(task);
        return status == StatusUtil.Status.PENDING || status == StatusUtil.Status.RUNNING;
    }
}
