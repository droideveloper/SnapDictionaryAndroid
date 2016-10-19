package org.fs.dictionary.model;

import org.fs.ghanaian.core.CoreObject;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * Created by Fatih on 26/11/14.
 */
public class LookupObject extends CoreObject {

    /***
     http://ws.tureng.com/TurengSearchServiceV4.svc/Search

     POST /TurengSearchServiceV4.svc/Search HTTP/1.1
     Content-type: application/json
     Accept: application/json
     Content-Length: 59
     Host: ws.tureng.com
     Connection: Keep-Alive
     User-Agent: Apache-HttpClient/UNAVAILABLE (java 1.4)

     {"Term":"hello", "Code":"cd787ce613a7c11400703d6202f5cc3a"}
     */

    private final static String KEY = "46E59BAC-E593-4F4F-A4DB-960857086F9C";

    public String mTerm;
    public String mCode;

    public LookupObject(String mTerm) {
        setTerm(mTerm);
        setCode(c(mTerm + KEY));
    }

    public String getTerm() {
        return mTerm;
    }

    public void setTerm(String mTerm) {
        this.mTerm = mTerm;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    @Override
    public boolean isLogEnabled() {
        return false;
    }

    @Override
    public String getClassTag() {
        return null;
    }

    /**
     * Authentication thing for Tureng's web service.
     * @param m
     * @return
     */
    private String c(String m) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(m.getBytes("Cp1254"));
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuffer localStringBuffer = new StringBuffer();

            for(int i = 0; i < arrayOfByte.length; i++) {
                String hex = Integer.toHexString(0xFF & arrayOfByte[i]);
                if(hex.length() >= 2) {
                    localStringBuffer.append(hex);
                } else {
                    localStringBuffer.append("0" + hex);
                }
            }
            return localStringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * parse the object into json String we needed for parameter.
     * @return
     */
    public String toJson() {
        return String.format(Locale.ENGLISH, "{\"Term\":\"%s\",\"Code\":\"%s\"}", getTerm(), getCode());
    }
}
