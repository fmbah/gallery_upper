package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName WxTmp
 * @Description
 * @Author root
 * @Date 18-12-24 上午10:42
 * @Version 1.0
 **/
public class WxTmp extends BaseBean {

    private String fontToPics;
    private String filterPic;

    public String getFontToPics() {
        return fontToPics;
    }

    public void setFontToPics(String fontToPics) {
        this.fontToPics = fontToPics;
    }

    public String getFilterPic() {
        return filterPic;
    }

    public void setFilterPic(String filterPic) {
        this.filterPic = filterPic;
    }
}
