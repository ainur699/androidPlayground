package com.vicman.jnitest.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.vicman.jnitest.utils.Utils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import java.io.IOException;

/*
<?xml version="1.0"?>
<image_process_response>
 <request_id>fe582a86-3c65-4e6b-a165-f45f5fe2e3b5</request_id>
 <status>OK</status>
 <description/>
 <err_code>0</err_code>
</image_process_response>
 */
@Root(name = "image_process_response", strict = false)
public class ProcessQueueResult {
    private static final String NO_ERROR_CODE = "0";

    @Element(name = "request_id", required = false)
    private String mRequestId;

    @Element(name = "status")
    private String mStatus;

    @Element(name = "description", required = false)
    private String mDescription;

    @Element(name = "err_code")
    private String mErrorCode;

    public int getErrorCode() {
        return Integer.parseInt(this.mErrorCode);
    }

    public String getRequestId() {
        return this.mRequestId;
    }

    public String getStatus() {
        return this.mStatus;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public boolean isOk() {
        return NO_ERROR_CODE.equals(this.mErrorCode);
    }

    public static void throwException(final @NonNull Context context, final @Nullable ProcessQueueResult result) throws IllegalStateException, IOException {
        if (result == null) {
            if (Utils.isInternetConnectionAvailable(context)) {
                throw new IllegalStateException();
            } else {
                throw new IOException("No internet connection");
            }
        }

        if (!result.isOk()) {
            throw new IOException(result.mErrorCode + " " + result.mDescription);
        }
    }

    @Override
    public String toString() {
        return "ProcessQueueResult{"
        + "err_code = " + this.mErrorCode
        + ", request_id = "+ this.mRequestId
        + ", status = " + this.mStatus
        + ", description = " + this.mDescription
        + "}";
    }
}

