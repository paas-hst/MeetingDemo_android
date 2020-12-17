package com.hst.meetingdemo.business;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hst.meetingdemo.utils.FspUtils;
import com.hst.meetingdemo.utils.VoiceVariantUtils;

import com.hst.meetingdemo.MeetingDemoApplication;

public class FspPreferenceManager {

    private static FspPreferenceManager s_instance = null;

    private SharedPreferences.Editor m_editor;
    private SharedPreferences m_sharedPreferences;


    public FspPreferenceManager() {
        m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MeetingDemoApplication.sApplication);
        m_editor = m_sharedPreferences.edit();
    }

    public static FspPreferenceManager getInstance() {
        if (s_instance == null) {
            synchronized (FspPreferenceManager.class) {
                if (s_instance == null) {
                    s_instance = new FspPreferenceManager();
                }
            }
        }
        return s_instance;
    }

    public boolean getAppConfig() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_APPCONFIG, true);
    }

    public String getAppId() {
        return m_sharedPreferences.getString(FspConstants.PKEY_USER_APPID, "");
    }

    public String getAppSecret() {
        return m_sharedPreferences.getString(FspConstants.PKEY_USER_APPSECRET, "");
    }

    public String getAppServerAddr() {
        return m_sharedPreferences.getString(FspConstants.PKEY_USER_APPSERVERADDR, "");
    }

    public boolean getDefaultOpenCamera() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_OPENCAMERA, false);
    }

    public boolean getDefaultOpenMIC() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_OPENMIC, false);
    }

    public boolean getIsForceLogin() {
        return m_sharedPreferences.getBoolean(FspConstants.PKEY_IS_FORCELOGIN, true);
    }

    public int getIsRecvVoiceVariant() {
        String strTmp = m_sharedPreferences.getString(FspConstants.PKEY_IS_RECVVOICEVARIANT,
                VoiceVariantUtils.s_voice_variant_list[0]);

        if(FspUtils.isSameText(strTmp, VoiceVariantUtils.s_voice_variant_list[1]))
            return 1;
        else if(FspUtils.isSameText(strTmp, VoiceVariantUtils.s_voice_variant_list[2]))
            return 2;
        else
            return 0;
    }

    public FspPreferenceManager setAppConfig(boolean config) {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_APPCONFIG, config);
        return this;
    }

    public FspPreferenceManager setAppId(String appId) {
        m_editor.putString(FspConstants.PKEY_USER_APPID, appId);
        return this;
    }

    public FspPreferenceManager setAppSecret(String appId) {
        m_editor.putString(FspConstants.PKEY_USER_APPSECRET, appId);
        return this;
    }

    public FspPreferenceManager setAppServerAddr(String appServerAddr) {
        m_editor.putString(FspConstants.PKEY_USER_APPSERVERADDR, appServerAddr);
        return this;
    }

    public FspPreferenceManager setDefaultOpenCamera(boolean open) {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_OPENCAMERA, open);
        return this;
    }

    public FspPreferenceManager setDefaultOpenMIC(boolean open) {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_OPENMIC, open);
        return this;
    }

    public FspPreferenceManager setForceLogin(boolean isForceLogin) {
        m_editor.putBoolean(FspConstants.PKEY_IS_FORCELOGIN, isForceLogin);
        return this;
    }

    public FspPreferenceManager setRecvVoiceVariant(String RecvVoiceVariant) {
        m_editor.putString(FspConstants.PKEY_IS_RECVVOICEVARIANT, RecvVoiceVariant);
        return this;
    }

    public void apply() {
        m_editor.apply();
    }

    public void commit() {
        m_editor.commit();
    }

}
