package common;

import java.io.Serializable;

public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String phone;

    public ClientInfo() {}
    public ClientInfo(String name, String phone) { this.name = name; this.phone = phone; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    @Override
    public String toString() { return name + (phone==null? "" : " ("+phone+")"); }
}

