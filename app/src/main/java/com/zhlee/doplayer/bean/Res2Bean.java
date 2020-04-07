package com.zhlee.doplayer.bean;

public class Res2Bean extends BaseBean {
    private String src_tv;
    private String usbp;

    public String getSrc_tv() {
        return src_tv;
    }

    public void setSrc_tv(String src_tv) {
        this.src_tv = src_tv;
    }

    public String getUsbp() {
        return usbp;
    }

    public void setUsbp(String usbp) {
        this.usbp = usbp;
    }

    @Override
    public String toString() {
        return "Res2Bean{" +
                "src_tv='" + src_tv + '\'' +
                ", usbp='" + usbp + '\'' +
                '}';
    }
}
