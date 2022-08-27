package com.eldaas.attendance;

public class PersonModelClass {
    public String name, email, id, organization, empUid, phone, embeddings, admin,father,bloodGrp,startDate;

    public PersonModelClass() {
    }

    public PersonModelClass(String name, String email, String id, String organization, String empUid, String phone, String embeddings, String admin, String father, String bloodGrp, String startDate) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.organization = organization;
        this.empUid = empUid;
        this.phone = phone;
        this.embeddings = embeddings;
        this.admin = admin;
        this.father = father;
        this.bloodGrp = bloodGrp;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmpUid() {
        return empUid;
    }

    public void setEmpUid(String empUid) {
        this.empUid = empUid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(String embeddings) {
        this.embeddings = embeddings;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
