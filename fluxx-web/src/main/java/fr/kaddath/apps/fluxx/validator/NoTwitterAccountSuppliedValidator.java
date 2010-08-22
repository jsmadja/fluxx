package fr.kaddath.apps.fluxx.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

import fr.kaddath.apps.fluxx.resource.FluxxMessage;

public class NoTwitterAccountSuppliedValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Boolean checkBoxValue = (Boolean) value;
        String twitterAccountId = (String) component.getAttributes().get("twitterAccount");
        UIInput twitterAccountField = (UIInput) context.getViewRoot().findComponent(twitterAccountId);
        String twitterAccount = twitterAccountField.getValue().toString();

        if (checkBoxValue && StringUtils.isBlank(twitterAccount)) {
            throw new ValidatorException(new FacesMessage(FluxxMessage.m("constraint_invalid_twitter_notification_notwitteraccount")));
        }
    }
}
