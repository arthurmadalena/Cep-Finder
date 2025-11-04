package br.com.arthur.madalena.cepmanager.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

@FacesConverter("cepConverter")
public class CepConverter implements Converter<String> {

    @Override
    public String getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value.replaceAll("[^0-9]", "");
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || value.length() != 8) {
            return value;
        }
        return value.substring(0, 5) + "-" + value.substring(5);
    }
}

