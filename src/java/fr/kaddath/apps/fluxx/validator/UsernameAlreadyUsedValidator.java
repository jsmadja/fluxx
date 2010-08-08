package fr.kaddath.apps.fluxx.validator;

import fr.kaddath.apps.fluxx.resource.FluxxMessage;
import fr.kaddath.apps.fluxx.service.Services;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.lang.StringUtils;

public class UsernameAlreadyUsedValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String username = (String) value;        
        if (StringUtils.isNotBlank(username)) {
            if(Services.userService.findByUsername(username) != null) {
                throw new ValidatorException(new FacesMessage(FluxxMessage.m("constraint_invalid_username_alreadyused")));
            }
        }
    }
}
