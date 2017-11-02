/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.experimental.slicesapp;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.slice.Slice;
import android.app.slice.Slice.Builder;
import android.app.slice.SliceProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.function.Consumer;


public class SlicesProvider extends SliceProvider {

    private static final String TAG = "SliceProvider";
    public static final String SLICE_INTENT = "android.intent.action.EXAMPLE_SLICE_INTENT";
    public static final String SLICE_ACTION = "android.intent.action.EXAMPLE_SLICE_ACTION";
    public static final String INTENT_ACTION_EXTRA = "android.intent.slicesapp.INTENT_ACTION_EXTRA";

    private final int NUM_LIST_ITEMS = 10;

    private SharedPreferences mSharedPrefs;

    @Override
    public boolean onCreate() {
        mSharedPrefs = getContext().getSharedPreferences("slice", 0);
        return true;
    }

    private Uri getIntentUri() {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(getContext().getPackageName())
                .appendPath("main").appendPath("intent")
                .build();
    }

    //@Override
    public Uri onMapIntentToUri(Intent intent) {
        if (intent.getAction().equals(SLICE_INTENT)) {
            return getIntentUri();
        }
        return null;//super.onMapIntentToUri(intent);
    }

    /**
     * Overriding onBindSlice will generate one Slice for all modes.
     * @param sliceUri
     */
    @Override
    public Slice onBindSlice(Uri sliceUri) {
        Log.w(TAG, "onBindSlice uri: " + sliceUri);
        String type = mSharedPrefs.getString("slice_type", "Default");
        if ("Default".equals(type)) {
            return null;
        }
        Slice.Builder b = new Builder(sliceUri);
        if (mSharedPrefs.getBoolean("show_header", false)) {
            b.addText("Header", Slice.HINT_TITLE);
            if (mSharedPrefs.getBoolean("show_sub_header", false)) {
                b.addText("Sub-header");
            }
        }
        if (sliceUri.equals(getIntentUri())) {
            type = "Intent";
        }
        switch (type) {
            case "Single-line":
                b.addSubSlice(makeList(new Slice.Builder(b), this::makeSingleLine,
                        this::addIcon));
                addPrimaryAction(b);
                break;
            case "Single-line action":
                b.addSubSlice(makeList(new Slice.Builder(b), this::makeSingleLine,
                        this::addAltActions));
                addPrimaryAction(b);
                break;
            case "Two-line":
                b.addSubSlice(makeList(new Slice.Builder(b), this::makeTwoLine,
                        this::addIcon));
                addPrimaryAction(b);
                break;
            case "Two-line action":
                b.addSubSlice(makeList(new Slice.Builder(b), this::makeTwoLine,
                        this::addAltActions));
                addPrimaryAction(b);
                break;
            case "Weather":
                b.addSubSlice(createWeather(new Slice.Builder(b)));
                break;
            case "Messaging":
                b.addSubSlice(createConversation(new Slice.Builder(b)));
                break;
            case "Keep actions":
                b.addSubSlice(createKeepNote(new Slice.Builder(b)));
                break;
            case "Maps multi":
                b.addSubSlice(createMapsMulti(new Slice.Builder(b)));
                break;
            case "Intent":
                b.addSubSlice(createIntentSlice(new Slice.Builder(b)));
                break;
            case "Settings":
                createSettingsSlice(b);
                break;
            case "Settings content":
                createSettingsContentSlice(b);
                break;
        }
        if (mSharedPrefs.getBoolean("show_action_row", false)) {
            Intent intent = new Intent(getContext(), SlicesActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
            b.addSubSlice(new Slice.Builder(b).addHints(Slice.HINT_ACTIONS)
                    .addAction(pendingIntent, new Slice.Builder(b)
                            .addText("Action1")
                            .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_add))
                            .build())
                    .addAction(pendingIntent, new Slice.Builder(b)
                            .addText("Action2")
                            .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_remove))
                            .build())
                    .addAction(pendingIntent, new Slice.Builder(b)
                            .addText("Action3")
                            .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_add))
                            .build())
                    .build());
        }
        return b.build();
    }

    private Slice createWeather(Builder grid) {
        grid.addHints(Slice.HINT_HORIZONTAL);
        grid.addSubSlice(new Slice.Builder(grid)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.weather_1),
                        Slice.HINT_LARGE)
                .addText("MON")
                .addText("69\u00B0", Slice.HINT_LARGE).build());
        grid.addSubSlice(new Slice.Builder(grid)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.weather_2),
                        Slice.HINT_LARGE)
                .addText("TUE")
                .addText("71\u00B0", Slice.HINT_LARGE).build());
        grid.addSubSlice(new Slice.Builder(grid)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.weather_3),
                        Slice.HINT_LARGE)
                .addText("WED")
                .addText("76\u00B0", Slice.HINT_LARGE).build());
        grid.addSubSlice(new Slice.Builder(grid)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.weather_4),
                        Slice.HINT_LARGE)
                .addText("THU")
                .addText("69\u00B0", Slice.HINT_LARGE).build());
        grid.addSubSlice(new Slice.Builder(grid)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.weather_2),
                        Slice.HINT_LARGE)
                .addText("FRI")
                .addText("71\u00B0", Slice.HINT_LARGE).build());
        return grid.build();
    }

    private Slice createConversation(Builder b2) {
        b2.addHints(Slice.HINT_LIST);
        b2.addSubSlice(new Slice.Builder(b2)
                .addHints(Slice.HINT_MESSAGE)
                .addText("yo home \uD83C\uDF55, I emailed you the info")
                .addTimestamp(System.currentTimeMillis() - 20 * DateUtils.MINUTE_IN_MILLIS)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.mady), Slice.HINT_SOURCE,
                        Slice.HINT_TITLE, Slice.HINT_LARGE)
                .build());
        b2.addSubSlice(new Builder(b2)
                .addHints(Slice.HINT_MESSAGE)
                .addText("just bought my tickets")
                .addTimestamp(System.currentTimeMillis() - 10 * DateUtils.MINUTE_IN_MILLIS)
                .build());
        b2.addSubSlice(new Builder(b2)
                .addHints(Slice.HINT_MESSAGE)
                .addText("yay! can't wait for getContext() weekend!\n"
                        + "\uD83D\uDE00")
                .addTimestamp(System.currentTimeMillis() - 5 * DateUtils.MINUTE_IN_MILLIS)
                .addIcon(Icon.createWithResource(getContext(), R.drawable.mady), Slice.HINT_SOURCE,
                        Slice.HINT_LARGE)
                .build());
        RemoteInput ri = new RemoteInput.Builder("someKey").setLabel("someLabel")
                .setAllowFreeFormInput(true).build();
        b2.addRemoteInput(ri);
        return b2.build();
    }

    private Slice addIcon(Builder b) {
        b.addIcon(Icon.createWithResource(getContext(), R.drawable.ic_add));
        return b.build();
    }

    private void addAltActions(Builder builder) {
        Intent intent = new Intent(getContext(), SlicesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        builder.addSubSlice(new Slice.Builder(builder).addHints(Slice.HINT_ACTIONS)
                .addAction(pendingIntent, new Slice.Builder(builder)
                        .addText("Alt1")
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_add)).build())
                .addAction(pendingIntent, new Slice.Builder(builder)
                        .addText("Alt2")
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_remove))
                        .build())
                .build());
    }

    private void makeSingleLine(Builder b) {
        b.addText("Single-line list item text", Slice.HINT_TITLE);
    }

    private void makeTwoLine(Builder b) {
        b.addText("Two-line list item text", Slice.HINT_TITLE);
        b.addText("Secondary text");
    }

    private void addPrimaryAction(Builder b) {
        Intent intent = new Intent(getContext(), SlicesActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
        b.addSubSlice(new Slice.Builder(b).addAction(pi,
                new Slice.Builder(b).addColor(0xFFFF5722)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_slice),
                                Slice.HINT_TITLE)
                        .addText("Slice App", Slice.HINT_TITLE)
                        .build()).addHints(Slice.HINT_HIDDEN, Slice.HINT_TITLE).build());
    }

    private Slice makeList(Builder list, Consumer<Builder> lineCreator,
            Consumer<Builder> lineHandler) {
        list.addHints(Slice.HINT_LIST);
        for (int i = 0; i < NUM_LIST_ITEMS; i++) {
            Builder b = new Builder(list);
            lineCreator.accept(b);
            lineHandler.accept(b);
            list.addSubSlice(b.build());
        }
        return list.build();
    }

    private Slice createKeepNote(Builder b) {
        Intent intent = new Intent(getContext(), SlicesActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
        RemoteInput ri = new RemoteInput.Builder("someKey").setLabel("someLabel")
                .setAllowFreeFormInput(true).build();
        return b.addText("Create new note", Slice.HINT_TITLE).addText("with keep")
                .addColor(0xffffc107)
                .addAction(pi, new Slice.Builder(b)
                        .addText("List")
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_list))
                        .build())
                .addAction(pi, new Slice.Builder(b)
                        .addText("Voice note")
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_voice))
                        .build())
                .addAction(pi, new Slice.Builder(b)
                        .addText("Camera")
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_camera))
                        .build())
                .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_create))
                .addRemoteInput(ri)
                .build();
    }

    private Slice createMapsMulti(Builder b) {
        Intent intent = new Intent(getContext(), SlicesActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);
        b.addHints(Slice.HINT_HORIZONTAL, Slice.HINT_LIST);
        b.addSubSlice(new Slice.Builder(b)
                .addAction(pi, new Slice.Builder(b)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_home)).build())
                .addText("Home", Slice.HINT_LARGE)
                .addText("25 min").build());
        b.addSubSlice(new Slice.Builder(b)
                .addAction(pi, new Slice.Builder(b)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_work)).build())
                .addText("Work", Slice.HINT_LARGE)
                .addText("1 hour 23 min").build());
        b.addSubSlice(new Slice.Builder(b)
                .addAction(pi, new Slice.Builder(b)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_car)).build())
                .addText("Mom's", Slice.HINT_LARGE)
                .addText("37 min").build());
        b.addColor(0xff0B8043);
        return b.build();
    }

    private Slice createIntentSlice(Builder b) {
        Intent intent = new Intent(getContext(), SlicesActivity.class);

        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);

        b.addHints(Slice.HINT_HORIZONTAL, Slice.HINT_LIST).addColor(0xff0B8043);
        b.addSubSlice(new Slice.Builder(b)
                .addAction(pi, new Slice.Builder(b)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_next)).build())
                .addText("Next", Slice.HINT_LARGE).build());
        b.addSubSlice(new Slice.Builder(b)
                .addAction(pi, new Slice.Builder(b)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_play)).build())
                .addText("Play", Slice.HINT_LARGE).build());
        b.addSubSlice(new Slice.Builder(b)
                .addAction(pi, new Slice.Builder(b)
                        .addIcon(Icon.createWithResource(getContext(), R.drawable.ic_prev)).build())
                .addText("Previous", Slice.HINT_LARGE).build());
        return b.build();
    }

    private Slice.Builder createSettingsSlice(Builder b) {
        b.addSubSlice(new Slice.Builder(b)
                .addAction(getIntent("toggled"), new Slice.Builder(b)
                        .addText("Wi-fi")
                        .addText("GoogleGuest")
                        .addHints(Slice.HINT_TOGGLE, Slice.HINT_SELECTED)
                        .build())
                .build());
        return b;
    }

    private Slice.Builder createSettingsContentSlice(Builder b) {
        b.addSubSlice(new Slice.Builder(b)
                .addAction(getIntent("main content"),
                        new Slice.Builder(b)
                                .addText("Wi-fi")
                                .addText("GoogleGuest")
                                .build())
                .addAction(getIntent("toggled"),
                        new Slice.Builder(b)
                                .addHints(Slice.HINT_TOGGLE, Slice.HINT_SELECTED)
                                .build())
                .build());
        return b;
    }

    private PendingIntent getIntent(String message) {
        Intent intent = new Intent(SLICE_ACTION);
        intent.setClass(getContext(), SlicesBroadcastReceiver.class);
        intent.putExtra(INTENT_ACTION_EXTRA, message);
        PendingIntent pi = PendingIntent.getBroadcast(getContext(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        return pi;
    }
}
