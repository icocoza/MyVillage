package com.ccz.myvillage.dto;


import com.ccz.myvillage.IConst;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BoardFile implements Serializable {
    @JsonProperty   private String fileid;
    @JsonProperty 	private String filetype, fileserver; //file server ip
    @JsonProperty 	private long filesize;
    @JsonProperty 	private String comment;

    @JsonProperty public String getFileid() {   return fileid;  }
    @JsonProperty public String getFiletype() { return filetype;    }
    @JsonProperty public String getFileserver() {   return fileserver;  }
    @JsonProperty public long getFilesize() {   return filesize;    }
    @JsonProperty public String getComment() {  return comment; }

    public String getFileUrl() {
        return String.format("http://%s:8080/thumb?fileid=%s&scode=%s", fileserver, fileid, IConst.ServiceCode);
    }
}
