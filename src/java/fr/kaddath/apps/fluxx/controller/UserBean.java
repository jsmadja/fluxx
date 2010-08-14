package fr.kaddath.apps.fluxx.controller;

import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.security.CryptographicService;
import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;

@Named(value="user")
@SessionScoped
@ManagedBean
public class UserBean extends ConnectedFluxxerBean implements Serializable {

    private String EDIT_ACCOUNT = "edit-account";
    private String EDIT_ACCOUNT_VALIDATION = "edit-account-validation";

    private String password;
    private String username;
    private String email;
    private String twitterAccount;
    private Boolean twitterNotification;
    private Boolean mailNotification;

    public String subscribe() {
        Fluxxer user = fillFluxxer();
        userService.persist(user);
        return "success";
    }

    private Fluxxer fillFluxxer() {
        Fluxxer user = new Fluxxer();
        String hashPassword = CryptographicService.toMD5(password);
        user.setUsername(username);
        user.setPassword(hashPassword);
        user.setSigninDate(new Date());
        user.setLastLoginDate(new Date());
        user.setEmail(email);
        user.setTwitterAccount(twitterAccount);
        user.setTwitterNotification(twitterNotification);
        user.setMailNotification(mailNotification);
        return user;
    }

    public String edit() {
        Fluxxer fluxxer = getFluxxer();
        reload(fluxxer);
        return EDIT_ACCOUNT;
    }

    private void reload(Fluxxer fluxxer) {
        setEmail(fluxxer.getEmail());
        setMailNotification(fluxxer.getMailNotification());
        setPassword(fluxxer.getPassword());
        setTwitterAccount(fluxxer.getTwitterAccount());
        setTwitterNotification(fluxxer.getTwitterNotification());
        setUsername(fluxxer.getUsername());
    }

    public String update() {
        Fluxxer user = fillFluxxer();
        userService.update(user);
        return EDIT_ACCOUNT_VALIDATION;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getMailNotification() {
        return mailNotification;
    }

    public void setMailNotification(Boolean mailNotification) {
        this.mailNotification = mailNotification;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public void setTwitterAccount(String twitterAccount) {
        this.twitterAccount = twitterAccount;
    }

    public Boolean getTwitterNotification() {
        return twitterNotification;
    }

    public void setTwitterNotification(Boolean twitterNotification) {
        this.twitterNotification = twitterNotification;
    }
}
