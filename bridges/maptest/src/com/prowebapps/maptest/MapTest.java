package com.prowebapps.maptest;

import android.app.Activity;
import android.os.Bundle;
import com.phonegap.*;

public class MapTest extends DroidGap
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}