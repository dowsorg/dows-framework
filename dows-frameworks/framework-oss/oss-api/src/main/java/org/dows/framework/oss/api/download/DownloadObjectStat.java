package org.dows.framework.oss.api.download;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class DownloadObjectStat implements Serializable {

    private static final long serialVersionUID = -2883494783412999919L;

    private long size;
    private Date lastModified;
    private String digest;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((digest == null) ? 0 : digest.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        return result;
    }

}
