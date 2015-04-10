package com.ak.vdrApp.service;

import com.ak.vdrApp.model.Project;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.IOException;


@Named
@RequestScoped
public class ImageStreamer {

    @EJB
    private ProjectManagerLocal projectManager;

    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // Rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // Browser is requesting the image. Return a real StreamedContent with the image bytes.
            String id= context.getExternalContext().getRequestParameterMap().get("id");
            Project project = projectManager.find(Integer.valueOf(id));
            return new DefaultStreamedContent(new ByteArrayInputStream((byte[]) project.getLogo()));
        }
    }

}