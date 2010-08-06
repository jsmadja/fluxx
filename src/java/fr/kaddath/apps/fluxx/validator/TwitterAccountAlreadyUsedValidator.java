package fr.kaddath.apps.fluxx.validator;

import fr.kaddath.apps.fluxx.exception.FluxxerNotFoundException;
import fr.kaddath.apps.fluxx.resource.FluxxMessage;
import fr.kaddath.apps.fluxx.service.Services;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TwitterAccountAlreadyUsedValidator implements Validator {

    private static final Logger LOG = Logger.getLogger("fluxx");

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String twitterAccount = (String) value;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Try to validate twitter account " + twitterAccount);
        }

        if (StringUtils.isNotBlank(twitterAccount)) {
            try {
                Services.userService.findByTwitterAccount(twitterAccount);
                throw new ValidatorException(new FacesMessage(FluxxMessage.m("constraint_invalid_twitteraccount")));
            } catch (FluxxerNotFoundException ex) {
            }
        }
    }
}
