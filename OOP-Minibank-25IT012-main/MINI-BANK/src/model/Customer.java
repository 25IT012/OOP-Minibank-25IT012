package model;

import java.io.Serializable;
import model.annotation.Id;
import model.annotation.MaxLength;

public class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String customerId;
    @MaxLength(30)
    private String name;
    private String email;
    private String mobile;
    private Address address;
    private static long customerCounter = 0;

    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;

        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }

    private static String generateCustomerId() {
        customerCounter++;
        return "CUST" + (100 + customerCounter);
    }

    public Customer(String name, String email, String mobile) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public Customer(String customerId, String name, String email, String mobile) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public Customer(String name) {
        this(name, "", "");
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (this.address != null) {
                copy.address = new Address(this.address.getLine(), this.address.getCity(), this.address.getPincode());
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "Customer[customerId=" + customerId + ", name=" + name + ", email=" + email + ", mobile=" + mobile + (address != null ? ", address=" + address : "") + "]";
    }
}
