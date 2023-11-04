package com.slot.twostepverification.crypto;


import com.slot.twostepverification.utils.encoding.EncodingException;

import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class CryptParameters implements Serializable {
    private byte[] _nonce;
    private byte[] _tag;

    public CryptParameters(byte[] nonce, byte[] tag) {
        _nonce = nonce;
        _tag = tag;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("nonce", Hex.encode(_nonce));
            obj.put("tag", Hex.encode(_tag));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return obj;
    }

    public static CryptParameters fromJson(JSONObject obj) throws JSONException, EncodingException {
        byte[] nonce = Hex.decode(obj.getString("nonce"));
        byte[] tag = Hex.decode(obj.getString("tag"));
        return new CryptParameters(nonce, tag);
    }

    public byte[] getNonce() {
        return _nonce;
    }

    public byte[] getTag() {
        return _tag;
    }
}