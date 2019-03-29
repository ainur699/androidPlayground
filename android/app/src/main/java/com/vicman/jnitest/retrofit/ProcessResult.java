package com.vicman.jnitest.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.vicman.jnitest.utils.Utils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* normal
<image_process_response>
  <request_id>472f9104-630c-427f-a6cf-07c6036bc66f</request_id>
  <status>OK</status>
  <result_url>http://worker-images.ws.pho.to/i1/8B9FDCC9-531D-4C7E-A004-40373E889F96.jpg</result_url>
  <limited_image_url>http://worker-images.ws.pho.to/i1/8B9FDCC9-531D-4C7E-A004-40373E889F96.jpg</limited_image_url>
  <nowm_image_url>http://worker-images.ws.pho.to/i1/8B9FDCC9-531D-4C7E-A004-40373E889F96.jpg</nowm_image_url>
</image_process_response>
*/

/* error
<?xml version="1.0"?>
<image_process_response>
  <status>SecurityError</status>
  <err_code>612</err_code>
  <description>Bad, invalid or empty REQUEST_ID parameter.</description>
</image_process_response>
*/

@Root(name = "image_process_response", strict = false)
public class ProcessResult {
    public static final @NonNull String STATUS_OK = "OK";
    public static final @NonNull String STATUS_IN_PROGRESS = "InProgress";

    @Element(name = "status")
    protected String mStatus;

    /* normal */
    @Element(name = "request_id", required = false)
    private String mRequestId;

    @Element(name = "result_url", required = false)
    private String mResultUrl;

    /* error */
    @Element(name = "err_code", required = false)
    private String mErrorCode;

    @Element(name = "description", required = false)
    private String mDescription;

    public @NonNull String getStatus() {
        return this.mStatus;
    }

    public @NonNull String getRequestId() {
        return this.mRequestId;
    }

    public @NonNull String getResultUrl() {
        return this.mResultUrl;
    }

    private @NonNull String getErrorCode() {
        return mErrorCode;
    }

    private String getDescription() {
        return mDescription;
    }

    public static void throwException(final @NonNull Context context, final @Nullable ProcessResult result) throws IllegalStateException, IOException {
        if (result == null) {
            if (Utils.isInternetConnectionAvailable(context)) {
                throw new IllegalStateException();
            } else {
                throw new IOException("No internet connection");
            }
        }

        if (TextUtils.isEmpty(result.mErrorCode)) {
            return;
        }

        throw new IOException(result.mErrorCode + " " + result.mDescription);
    }

    @Override
    public String toString() {
        return "ProcessResult{"
        + "status = " + this.mStatus
        + ", request_id = " + this.mRequestId
        + ", result_url = " + this.mResultUrl
        + ", err_code = " + this.mErrorCode
        + ", description = " + this.mDescription
        + "}";
    }

    // Emolfi emotion
    @Element(name = "emotion", required = false)
    public String emotion;

    @Element(name = "emotion_id", required = false)
    public String emotionId;

    @Element(name = "effects_id", required = false)
    public String effectsId;

    @Element(name = "vicman_fd", required = false)
    public String vicmanFd;

    @Element(name = "effects_id_fnf", required = false)
    public String effectsIdFnf;

    @Element(name = "tip", required = false)
    public String tip;

    @Element(name = "multifaces", required = false)
    public int multifaces;

    @ElementList(name="scores", required = false)
    public List<Score> scores;
    @Root(name="scores", strict=false)
    @Convert(ExternalConverter.class)
    public static class Score {
        public String trans;
        public String id;
        public float value;
    }

    public @Nullable String getEmotion() {
        return this.emotion;
    }
    public @Nullable String getEmotionId() {
        return this.emotionId;
    }
    public @Nullable String getEffectsId() {
        return this.effectsId;
    }
    public @Nullable String getVicmanFd() {
        return this.vicmanFd;
    }
    public @Nullable String getEffectsIdFnf() {
        return this.effectsIdFnf;
    }
    public @Nullable String getTip() {
        return this.tip;
    }
    public int getMultiFaces() {
        return this.multifaces;
    }
    public @Nullable List<Score> getScores() {
        if (scores != null) {
            Iterator<Score> iterator = this.scores.iterator();
            while (iterator.hasNext()) {
                Score next =  iterator.next();
                if (this.emotionId != null && next != null && next.id.equals(this.emotionId)) {
                    iterator.remove();
                }
            }
            Collections.sort(this.scores, new ScoreComparator());
        }
        return this.scores;
    }


    // InstagtamApp suggest
    @ElementList(name = "suggests", required = false)
    private List<Suggest> suggests;
    public @Nullable List<Suggest> getSuggests() {
        return this.suggests;
    }


    @Root(name="suggest", strict=false)
    public static class Suggest {
        @Element(name = "filter", required = false)
        public String filter;

        @Element(name = "score", required = false)
        public int score;

        @Element(name = "result_url", required = false)
        public String resultUrl;
    }


    public static class ExternalConverter implements Converter<Score> {

        public Score read(InputNode node) {
            Score score = new Score();
            try {
                score.id = node.getName();
                score.trans = node.getAttribute("trans").getValue();
                score.value = Float.parseFloat(node.getValue().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return score;
        }

        public void write(OutputNode node, Score external) {
        }
    }

    public static class ScoreComparator implements Comparator<Score>{
        @Override
        public int compare(Score o1, Score o2) {
            return o1 != null && o2 != null ? (o1.value > o2.value ? -1 : o1.value < o2.value ? 1 : 0) : 0;
        }
    }
}

