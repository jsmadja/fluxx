package fr.kaddath.apps.fluxx.controller;

import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.service.UserService;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

public class ConnectedFluxxerBean {

    @Inject
    protected UserService userService;

    public Fluxxer getFluxxer() {
        FacesContext context = FacesContext.getCurrentInstance();
        String remoteUser = context.getExternalContext().getRemoteUser();
        return userService.findByUsername(remoteUser);        
    }
}
