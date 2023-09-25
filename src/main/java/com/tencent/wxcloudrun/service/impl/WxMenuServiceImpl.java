package com.tencent.wxcloudrun.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.dao.WxMenuMapper;
import com.tencent.wxcloudrun.model.WxMenu;
import com.tencent.wxcloudrun.service.WxMenuService;
@Service
public class WxMenuServiceImpl extends ServiceImpl<WxMenuMapper, WxMenu> implements WxMenuService{

}
