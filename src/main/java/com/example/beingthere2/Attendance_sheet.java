package com.example.beingthere2;

class Attendance_sheet {
    String cn,flat,mad,ml,cg,cgl,cnl,madl;

    public Attendance_sheet(String cn, String flat, String mad, String ml, String cg, String cgl, String cnl, String madl) {
        this.cn=cn;
        this.flat=flat;
        this.mad=mad;
        this.ml=ml;
        this.cg=cg;
        this.cgl=cgl;
        this.cnl=cnl;
        this.madl=madl;
    }

    public String getCn() {
        return cn;
    }

    public String getCnl() {
        return cnl;
    }

    public String getMad() {
        return mad;
    }

    public String getMadl() {
        return madl;
    }

    public String getFlat() {
        return flat;
    }

    public String getCg() {
        return cg;
    }

    public String getCgl() {
        return cgl;
    }

    public String getMl() {
        return ml;
    }
}
