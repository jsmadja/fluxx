package fr.kaddath.apps.fluxx.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

import fr.kaddath.apps.fluxx.exception.FluxxerNotFoundException;
import fr.kaddath.apps.fluxx.resource.FluxxMessage;
import fr.kaddath.apps.fluxx.service.Services;

public class EmailAlreadyUsedValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String mailAccount = (String) value;
        if (StringUtils.isNotBlank(mailAccount)) {
            try {
                Services.getUserService().findByMailAccount(mailAccount);
                throw new ValidatorException(new FacesMessage(FluxxMessage.m("constraint_invalid_email_alreadyused")));
            } catch (FluxxerNotFoundException ex) {}
        }
    }
}
