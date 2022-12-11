package org.dows.framework.oss.api.download;

import lombok.Data;

@Data
public class DownloadPartResult {

    private int number;
    private long start;
    private long end;
    private boolean failed = false;
    private Exception exception;
    private Long clientCrc;
    private Long serverCrc;
    private long length;

    public DownloadPartResult(int number, long start, long end) {
        this.number = number;
        this.start = start;
        this.end = end;
    }

    public DownloadPartResult(int number, long start, long end, long length, long clientCrc) {
        this.number = number;
        this.start = start;
        this.end = end;
        this.length = length;
        this.clientCrc = clientCrc;
    }

}
