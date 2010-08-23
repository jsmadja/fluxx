package fr.kaddath.apps.fluxx.security;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import sun.security.acl.PrincipalImpl;

public class LoginModule implements javax.security.auth.spi.LoginModule {

    private static final Logger log = Logger.getLogger(LoginModule.class.getName());

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {

        log.info("initialize");

        Principal userPrincipal = new PrincipalImpl("julien");

        subject.getPrincipals().add(userPrincipal);

    }

    @Override
    public boolean login() throws LoginException {
        log.info("login");
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        log.info("commit");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean abort() throws LoginException {
        log.info("abord");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean logout() throws LoginException {
        log.info("logout");
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
