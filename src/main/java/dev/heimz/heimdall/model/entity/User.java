package dev.heimz.heimdall.model.entity;

public class User extends Subject {

    private Organization organization;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
