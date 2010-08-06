package fr.kaddath.apps.fluxx.view.listener;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpSession;

public class LogoutActionListener implements ActionListener {

    private static final Logger LOG = Logger.getLogger(LogoutActionListener.class.getName());

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            Object session = externalContext.getSession(false);
            HttpSession httpSession = (HttpSession) session;
            httpSession.invalidate();
            externalContext.redirect(externalContext.getRequestContextPath());
        } catch (IOException ex) {
            Logger.getLogger(LogoutActionListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
