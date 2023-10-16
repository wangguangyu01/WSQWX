package com.tencent.wxcloudrun.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tencent.wxcloudrun.dto.WxPersonalBrowsePageDTO;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.utils.DateUtils;
import com.tencent.wxcloudrun.vo.WxUserBrowsingUsersVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencent.wxcloudrun.dao.WxBrowsingUsersMapper;
import com.tencent.wxcloudrun.model.WxBrowsingUsers;
import com.tencent.wxcloudrun.service.WxBrowsingUsersService;
import org.springframework.util.ObjectUtils;

@Service
public class WxBrowsingUsersServiceImpl extends ServiceImpl<WxBrowsingUsersMapper, WxBrowsingUsers> implements WxBrowsingUsersService{

    @Autowired
    private WxBrowsingUsersMapper wxBrowsingUsersMapper;


    @Override
    public IPage<WxUserBrowsingUsersVo> queryWxUserPage(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) throws Exception {
        // 当前日期
        Calendar nowCalendar = Calendar.getInstance();
        // 当前年
        int yearNow = nowCalendar.get(Calendar.YEAR);
        Page page = new Page(wxPersonalBrowseDTO.getCurrentPage(), wxPersonalBrowseDTO.getLimit());
        IPage<WxUserBrowsingUsersVo> wxUserIPage = wxBrowsingUsersMapper.queryBrowsingUsersPage(page, wxPersonalBrowseDTO);

        List<WxUserBrowsingUsersVo> wxUserBrowsingUsersVos = wxUserIPage.getRecords();

        for (WxUserBrowsingUsersVo wxUserBrowsingUsersVo: wxUserBrowsingUsersVos) {
            if (StringUtils.equals(wxUserBrowsingUsersVo.getSex(), "男")) {
                wxUserBrowsingUsersVo.setBgColor("#8cefe19e");
            } else {
                wxUserBrowsingUsersVo.setBgColor("#ef8cc79e");
            }
            Date birthdayDate = DateUtils.parseDate(wxUserBrowsingUsersVo.getBirthday(), DateUtils.DATE_PATTERN);
            if (!ObjectUtils.isEmpty(birthdayDate)) {
                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthdayDate);
                if (nowCalendar.after(birthCalendar)) {
                    int yearBirth = birthCalendar.get(Calendar.YEAR);
                    int age = yearNow - yearBirth;
                    wxUserBrowsingUsersVo.setAge(age);
                }
            }
        }
        return wxUserIPage;
    }

    @Override
    public int queryCount(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) {
        return wxBrowsingUsersMapper.queryCount(wxPersonalBrowseDTO);
    }

    @Override
    public IPage<WxUserBrowsingUsersVo> queryBebrowsedPage(WxPersonalBrowsePageDTO wxPersonalBrowseDTO) {
        // 当前日期
        Calendar nowCalendar = Calendar.getInstance();
        // 当前年
        int yearNow = nowCalendar.get(Calendar.YEAR);
        Page page = new Page(wxPersonalBrowseDTO.getCurrentPage(), wxPersonalBrowseDTO.getLimit());
        IPage<WxUserBrowsingUsersVo> wxUserIPage = wxBrowsingUsersMapper.queryBebrowsedPage(page, wxPersonalBrowseDTO);
        List<WxUserBrowsingUsersVo> wxUserBrowsingUsersVos = wxUserIPage.getRecords();
        for (WxUserBrowsingUsersVo wxUserBrowsingUsersVo: wxUserBrowsingUsersVos) {
            if (StringUtils.equals(wxUserBrowsingUsersVo.getSex(), "男")) {
                wxUserBrowsingUsersVo.setBgColor("#8cefe19e");
            } else {
                wxUserBrowsingUsersVo.setBgColor("#ef8cc79e");
            }
            Date birthdayDate = DateUtils.parseDate(wxUserBrowsingUsersVo.getBirthday(), DateUtils.DATE_PATTERN);
            if (!ObjectUtils.isEmpty(birthdayDate)) {
                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthdayDate);
                if (nowCalendar.after(birthCalendar)) {
                    int yearBirth = birthCalendar.get(Calendar.YEAR);
                    int age = yearNow - yearBirth;
                    wxUserBrowsingUsersVo.setAge(age);
                }
            }
        }
        return wxUserIPage;
    }
}
