package fr.kaddath.apps.fluxx.validator;

import fr.kaddath.apps.fluxx.resource.FluxxMessage;
import fr.kaddath.apps.fluxx.service.Services;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class UsernameAlreadyUsedValidator implements Validator {

    private static final Logger LOG = Logger.getLogger("fluxx");

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String username = (String) value;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Try to validate username " + username);
        }
        
        if (StringUtils.isNotBlank(username)) {
            if(Services.userService.findByUsername(username) != null) {
                throw new ValidatorException(new FacesMessage(FluxxMessage.m("constraint_invalid_username_alreadyused")));
            }
        }
    }
}
