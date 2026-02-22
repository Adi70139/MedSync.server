package com.example.prescripto.Utils;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address1 = "Line1";
    private String address2 = "Line2";

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}
