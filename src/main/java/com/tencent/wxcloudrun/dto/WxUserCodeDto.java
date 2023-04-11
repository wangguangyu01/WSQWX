package com.tencent.wxcloudrun.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxUserCodeDto  implements Serializable {


    private String code;
}
