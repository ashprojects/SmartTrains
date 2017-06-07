package SmartTrainTools;

import java.io.IOException;

/**
 * Created by root on 26/5/17.
 */

public class TrainQuery extends Train {
    private Station querySrcStn,queryDestStn;

    public TrainQuery(String no, boolean fetchInfo, boolean fetchAsync) throws IOException {
        super(no, fetchInfo, fetchAsync);
    }


    public Station getQueryDestStn() {
        return queryDestStn;
    }

    public void setQueryDestStn(Station destStn) {
        this.queryDestStn = destStn;
    }

    public Station getQuerySrcStn() {
        return querySrcStn;
    }

    public void setQuerySrcStn(Station srcStn) {
        this.querySrcStn = srcStn;
    }

}
