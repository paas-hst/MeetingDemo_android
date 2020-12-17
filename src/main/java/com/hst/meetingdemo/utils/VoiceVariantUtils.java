package com.hst.meetingdemo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.hst.fsp.VideoProfile;
import com.hst.meetingdemo.R;

public class VoiceVariantUtils {

    public static final String s_voice_variant_list[] = {"跟随发送端", "只接收原声", "只接收变声"};

    private  static final int voice_variant_follow = 0;
    private  static final int voice_variant_origin_only = 1;
    private  static final int voice_variant_variant_only = 2;

    public static String getProfileRecently(int value) {
        if(value < 0 || value > 2)
            value = 0;
        return s_voice_variant_list[value];
    }

    public static void showProfileDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog);
        builder.setTitle("变调");
        builder.setItems(s_voice_variant_list, onClickListener);
        AlertDialog r_dialog = builder.create();
        r_dialog.show();
    }

    public static String getVoiceModeList(int position) {
        return s_voice_variant_list[position];
    }

}
