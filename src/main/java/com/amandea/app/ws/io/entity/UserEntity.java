package com.amandea.app.ws.io.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity(name="users")
public class UserEntity implements Serializable {
   private static final long serialVersionUID = 5313493413859894403L;
   @Id
   @GeneratedValue
   private long id;

   @Column(nullable = false)
   private String userId;

    @Column(nullable = false,length=50)
   private String firstName;

    @Column(nullable = false,length=50)
   private String lastName;

    @Column(nullable = false,length=120)
   private String email;

    @Column(nullable = false)
   private String encryptedPassword;

   private String emailVerificationToken;

    @Column(nullable = false)
   private Boolean EmailVerificationStatus = false;

    //if we want addresses to be persisted when the user details is persisted , we need to provide one attribute "cascade"
    //cascadeType = All means when persist operation takes place and the user details is persisted into the database, we will have a list of addresses persisted as well, and
    //if we delete the user details from the database, the operation will also propagate and list of addresses will be deleted from the database for that user.
    @OneToMany(mappedBy = "userDetails",cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Boolean getGetEmailVerificationStatus() {
        return EmailVerificationStatus;
    }

    public void setGetEmailVerificationStatus(Boolean getEmailVerificationStatus) {
        this.EmailVerificationStatus = getEmailVerificationStatus;
    }


    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }
}
