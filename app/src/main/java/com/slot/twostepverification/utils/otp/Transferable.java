package com.slot.twostepverification.utils.otp;

import android.net.Uri;

public interface Transferable {
    Uri getUri() throws GoogleAuthInfoException;
}
