package com.eldaas.attendance;
public class Users {
    public String name, email, id, organization, year, phone, embeddings, admin;

    public Users() {
    }

    public Users(String Name, String Email, String ID, String Organozation , String Year, String Phone, String Embeddings, String Admin) {
        this.name = Name;
        this.email = Email;
        this.id = ID;
        this.organization = Organozation;
        this.embeddings = Embeddings;
        this.year = Year;
        this.phone = Phone;
        this.admin = Admin;
    }
}
