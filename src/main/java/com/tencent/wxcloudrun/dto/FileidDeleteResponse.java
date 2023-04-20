package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FileidDeleteResponse implements Serializable {


    private Integer errcode;

    private String errmsg;


    private List<String> delete_list;
}
