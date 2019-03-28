package com.zianbam.yourcommunity.Model;

public class User {
    private String id;
    private String name;
    private String email;
    private String imageURL;
    private String bio;
    private String location;
    private String gender;
    private String username;
    private String pref, referralcode, referralclaim;
    private long energycharge;
    private long subscription, featurephoto;

    public User() {
    }

    public User(String id, String name, String email, String imageURL, String bio, String location, String gender, String username, String pref, String referralcode, String referralclaim, long energycharge, long subscription, long featurephoto) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageURL = imageURL;
        this.bio = bio;
        this.location = location;
        this.gender = gender;
        this.username = username;
        this.pref = pref;
        this.referralcode = referralcode;
        this.referralclaim = referralclaim;
        this.energycharge = energycharge;
        this.subscription = subscription;
        this.featurephoto = featurephoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPref() {
        return pref;
    }

    public void setPref(String pref) {
        this.pref = pref;
    }

    public String getReferralcode() {
        return referralcode;
    }

    public void setReferralcode(String referralcode) {
        this.referralcode = referralcode;
    }

    public String getReferralclaim() {
        return referralclaim;
    }

    public void setReferralclaim(String referralclaim) {
        this.referralclaim = referralclaim;
    }

    public long getEnergycharge() {
        return energycharge;
    }

    public void setEnergycharge(long energycharge) {
        this.energycharge = energycharge;
    }

    public long getSubscription() {
        return subscription;
    }

    public void setSubscription(long subscription) {
        this.subscription = subscription;
    }

    public long getFeaturephoto() {
        return featurephoto;
    }

    public void setFeaturephoto(long featurephoto) {
        this.featurephoto = featurephoto;
    }
}