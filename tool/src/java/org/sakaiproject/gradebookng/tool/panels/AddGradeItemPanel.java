package org.sakaiproject.gradebookng.tool.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.gradebookng.business.GradebookNgBusinessService;
import org.sakaiproject.gradebookng.tool.pages.GradebookPage;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.AssignmentHasIllegalPointsException;
import org.sakaiproject.service.gradebook.shared.ConflictingAssignmentNameException;
import org.sakaiproject.service.gradebook.shared.ConflictingExternalIdException;
import org.sakaiproject.tool.gradebook.Gradebook;

import java.text.MessageFormat;
import java.util.List;
import java.lang.Exception;

/**
 * The panel for the add grade item window
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public class AddGradeItemPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean(name="org.sakaiproject.gradebookng.business.GradebookNgBusinessService")
	protected GradebookNgBusinessService businessService;

	public AddGradeItemPanel(String id, final ModalWindow window) {
		super(id);

		Assignment assignment = new Assignment();

		// Default released to true
		assignment.setReleased(true);
		// If no categories, then default counted to true
		Gradebook gradebook = businessService.getGradebook();
		assignment.setCounted(GradebookService.CATEGORY_TYPE_NO_CATEGORY == gradebook.getCategory_type());

		Model<Assignment> model = new Model<Assignment>(assignment);

		Form form = new Form("addGradeItemForm", model);

		AjaxButton submit = new AjaxButton("submit") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form form) {
				Assignment assignment =  (Assignment) form.getModelObject();

				Long assignmentId = null;

				boolean success = true;
				try {
					assignmentId = businessService.addAssignment(assignment);
				} catch (AssignmentHasIllegalPointsException e) {
					error(new ResourceModel("error.addgradeitem.points").getObject());
					success = false;
				} catch (ConflictingAssignmentNameException e) {
					error(new ResourceModel("error.addgradeitem.title").getObject());
					success = false;
				} catch (ConflictingExternalIdException e) {
					error(new ResourceModel("error.addgradeitem.exception").getObject());
					success = false;
				} catch (Exception e) {
					error(new ResourceModel("error.addgradeitem.exception").getObject());
					success = false;
				}
				if (success) {
					getSession().info(MessageFormat.format(getString("notification.addgradeitem.success"), assignment.getName()));
					setResponsePage(getPage().getPageClass(), new PageParameters().add(GradebookPage.CREATED_ASSIGNMENT_ID_PARAM, assignmentId));
				} else {
					target.addChildren(form, FeedbackPanel.class);
				}
				
			}
		};
		form.add(submit);

		form.add(new AddGradeItemPanelContent("subComponents", model));

		FeedbackPanel feedback = new FeedbackPanel("addGradeFeedback") {
			private static final long serialVersionUID = 1L;

			@Override
			protected Component newMessageDisplayComponent(final String id, final FeedbackMessage message) {
				final Component newMessageDisplayComponent = super.newMessageDisplayComponent(id, message);

				if(message.getLevel() == FeedbackMessage.ERROR ||
								message.getLevel() == FeedbackMessage.DEBUG ||
								message.getLevel() == FeedbackMessage.FATAL ||
								message.getLevel() == FeedbackMessage.WARNING){
					add(AttributeModifier.replace("class", "messageError"));
					add(AttributeModifier.append("class", "feedback"));
				} else if(message.getLevel() == FeedbackMessage.INFO){
					add(AttributeModifier.replace("class", "messageSuccess"));
					add(AttributeModifier.append("class", "feedback"));
				}

				return newMessageDisplayComponent;
			}
		};
		feedback.setOutputMarkupId(true);
		form.add(feedback);

		AjaxButton cancel = new AjaxButton("cancel") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				window.close(target);
			}
		};
		form.add(cancel);
		add(form);
	}
}
