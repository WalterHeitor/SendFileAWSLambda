package br.com.softwalter.entity;

public enum ContractType {
    BACKEND_JAVA(1, "Back End Java"),
    FRONTEND_ANGULAR(2, "Front End Angular"),
    MOBILE_IOS(3, "Mobile iOS"),
    MOBILE_ANDROID(4, "Mobile Android"),
    DEVOPS(5, "DevOps")
    ;

    private final int id;
    private final String description;

    ContractType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}