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

package com.android.experimental.slicepresenter;

import android.app.Activity;
import android.app.slice.Slice;
import android.app.slice.widget.SliceView;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SlicePresenter extends Activity {

    private static final String TAG = "SlicePresenter";

    private static final String SLICE_METADATA_KEY = "android.metadata.SLICE_URI";

    private ArrayList<Uri> mSliceUris = new ArrayList<Uri>();
    private String mSelectedMode;
    private ViewGroup mContainer;
    private boolean mShowingIntentSlice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        // Shows the slice
        mContainer = (ViewGroup) findViewById(R.id.slice_preview);

        // Select the slice mode
        List<String> list = new ArrayList<>();
        list.add(SliceView.MODE_SHORTCUT);
        list.add(SliceView.MODE_SMALL);
        list.add(SliceView.MODE_LARGE);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mSelectedMode = (savedInstanceState != null)
                ? savedInstanceState.getString("SELECTED_MODE", list.get(0))
                : list.get(0);
        spinner.setSelection(list.indexOf(mSelectedMode));
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedMode = list.get(position);
                updateSliceModes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Toggle slice between URI based or Intent based
        final String intentButton = getResources().getString(R.string.intent_button);
        final String uriButton = getResources().getString(R.string.uri_button);
        Button button = (Button) findViewById(R.id.slice_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String state = (String) button.getText();
                final String newState = state.equals(intentButton) ? uriButton : intentButton;
                button.setText(newState);
                mShowingIntentSlice = newState.equals(uriButton);
                if (mShowingIntentSlice) {
                    showIntentSlice();
                } else {
                    showAvailableSlices();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("SELECTED_MODE", mSelectedMode);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShowingIntentSlice) {
            showIntentSlice();
        } else {
            showAvailableSlices();
        }
    }

    private void showAvailableSlices() {
        mContainer.removeAllViews();
        mSliceUris.clear();
        List<PackageInfo> packageInfos = getPackageManager()
                .getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        for (PackageInfo pi : packageInfos) {
            ActivityInfo[] activityInfos = pi.activities;
            if (activityInfos != null) {
                for (ActivityInfo ai : activityInfos) {
                    if (ai.metaData != null) {
                        String sliceUri = ai.metaData.getString(SLICE_METADATA_KEY);
                        if (sliceUri != null) {
                            mSliceUris.add(Uri.parse(sliceUri));
                        }
                    }
                }
            }
        }
        for (int i = 0; i < mSliceUris.size(); i++) {
            addSlice(mSliceUris.get(i));
        }
    }

    private void addSlice(Uri uri) {
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            SliceView v = new SliceView(getApplicationContext());
            v.setTag(uri);
            mContainer.addView(v);
            v.setMode(mSelectedMode);
            v.setSlice(uri);
        } else {
            Log.w(TAG, "Invalid uri, skipping slice: " + uri);
        }
    }

    private void showIntentSlice() {
        Intent intent = new Intent("android.intent.action.EXAMPLE_ACTION");
        mContainer.removeAllViews();
        SliceView v = new SliceView(getApplicationContext());
        v.setTag(intent);
        /*
        boolean added = v.setSlice(intent);
        if (added) {
            mContainer.addView(v);
            v.setMode(mSelectedMode);
        }
        */
    }

    private void updateSliceModes() {
        final int count = mContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            ((SliceView) mContainer.getChildAt(i)).setMode(mSelectedMode);
        }
    }
}
