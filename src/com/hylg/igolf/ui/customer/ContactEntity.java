package com.hylg.igolf.ui.customer;


import java.io.Serializable;

public class ContactEntity implements Serializable {

    public String userName;

    public char lable;

    public String phone;

//    public char getSortkeyHead() {
//        return lable.charAt(0);
//    }

    public char getSortkey() {
        return lable;
    }

}
