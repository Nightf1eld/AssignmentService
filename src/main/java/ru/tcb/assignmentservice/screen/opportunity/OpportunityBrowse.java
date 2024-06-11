package ru.tcb.assignmentservice.screen.opportunity;

import io.jmix.appsettings.AppSettings;
import io.jmix.core.DataManager;
import io.jmix.ui.Dialogs;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.DialogOutcome;
import io.jmix.ui.app.inputdialog.InputDialog;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.*;
import io.jmix.ui.icon.Icons;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.DataLoader;
import io.jmix.ui.screen.*;
import io.jmix.ui.screen.LookupComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.tcb.assignmentservice.domain.entities.ApplicationSettings;
import ru.tcb.assignmentservice.domain.entities.Employee;
import ru.tcb.assignmentservice.domain.entities.Opportunity;
import ru.tcb.assignmentservice.domain.entities.Queue;
import ru.tcb.assignmentservice.services.AssignmentService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@UiController("Opportunity.browse")
@UiDescriptor("opportunity-browse.xml")
@LookupComponent("opportunitiesTable")
public class OpportunityBrowse extends StandardLookup<Opportunity> {
    @Autowired
    private AppSettings settings;

    private long LOCK_DURATION;
    @Autowired
    private CollectionContainer<Employee> employeesDc;

    @Autowired
    private CollectionContainer<Queue> manualQueuesDc;

    @Autowired
    private DataLoader opportunitiesDl;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private DataGrid<Opportunity> opportunitiesTable;

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private DataLoader employeesDl;

    @Autowired
    private AssignmentService assignmentService;

    @Subscribe("queueSelectionField")
    public void onQueueSelectionFieldValueChange(HasValue.ValueChangeEvent<Queue> event) {
        Queue queue = event.getValue();
        if(queue != null)
            opportunitiesDl.setParameter("queue", queue);
        else
            opportunitiesDl.removeParameter("queue");
        opportunitiesDl.load();
    }

    @Subscribe
    public void onInit(InitEvent event){
        ApplicationSettings settings = this.settings.load(ApplicationSettings.class);
        LOCK_DURATION = settings.getLockDuration();
    }


    @Install(to = "opportunitiesTable.assign", subject = "enabledRule")
    private boolean opportunitiesTableAssignEnabledRule() {
        return opportunitiesTable.getSelected().size() > 0;
    }

    @Subscribe("opportunitiesTable.assign")
    public void onOpportunitiesTableAssign(Action.ActionPerformedEvent event) {
        if(!validateLocks())
            return;
        dialogs.createInputDialog(this)
                .withParameter(InputParameter.parameter("queue")
                        .withField(()->{
                            EntityComboBox<Queue> field = uiComponents.create(EntityComboBox.of(Queue.class));
                            field.setOptionsContainer(manualQueuesDc);
                            field.setCaption("Очередь");
                            field.setWidthFull();
                            field.addValueChangeListener(e->{
                                Queue queue = e.getValue();
                                if(queue == null ||queue.getEmployees().size() == 0)
                                    employeesDl.removeParameter("queue");
                                else
                                    employeesDl.setParameter("queue", queue);

                                employeesDl.load();
                            });
                            return field;
                        })
                )
                .withParameter(InputParameter.parameter("employee")
                        .withField(()->{
                            EntityComboBox<Employee> field = uiComponents.create(EntityComboBox.of(Employee.class));
                            field.setOptionsContainer(employeesDc);
                            field.setCaption("Сотрудник");
                            field.setWidthFull();
                            return field;
                        })
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(this::assignOpportunities)
                .build()
                .show();
    }

    private void updateAvailableEmployees(ValuePicker.FieldValueChangeEvent<Queue> event) {

    }

    private boolean validateLock(Opportunity opportunity){
        return !opportunity.getLocked() || opportunity.getLockedDate().plus(LOCK_DURATION, ChronoUnit.SECONDS).isBefore(LocalDateTime.now());
    }
    private boolean validateLocks() {
        boolean result;

        result = opportunitiesTable
                .getSelected()
                .stream()
                .map(this::validateLock)
                .reduce(true, (t, i) -> t && i);

        if(!result)
            dialogs.createMessageDialog()
                    .withCaption("Невозможно назначить заявки")
                    .withMessage("Одна или несколько заявок из выбранных находится в работе у сотрудника менее " + LOCK_DURATION / 60 +" минут")
                    .show();

        return result;
    }

    private void assignOpportunities(InputDialog.InputDialogCloseEvent event) {
        if(event.closedWith(DialogOutcome.OK)){
            /* for(Opportunity opty: opportunitiesTable.getSelected()){
                if(event.getValue("queue") != null)
                    opty.setCurrentQueue(event.getValue("queue"));
                if(event.getValue("employee") != null)
                    opty.setCurrentEmployee(event.getValue("employee"));
                dataManager.save(opty);
            } */
            assignmentService.assignManual(opportunitiesTable.getSelected(), event.getValue("queue"), event.getValue("employee"));
        }
    }

    @Subscribe("opportunitiesTable.assignAuto")
    public void onOpportunitiesTableAssignAuto(Action.ActionPerformedEvent event) {
        if(validateLocks())
            for(Opportunity opportunity: opportunitiesTable.getSelected())
                assignmentService.assignAutomatic(opportunity, false);
    }

    @Subscribe("searchField")
    public void onSearchFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        if(event.getValue() != null && !event.getValue().isBlank())
            opportunitiesDl.setParameter("name", event.getValue());
        else
            opportunitiesDl.removeParameter("name");
        opportunitiesDl.load();
    }

    @Install(to = "opportunitiesTable.status", subject = "columnGenerator")
    private Icons.Icon iconGridHasEmailColumnGenerator(DataGrid.ColumnGeneratorEvent<Opportunity> event) {
        if(!event.getItem().getLocked())return null;
        if(validateLock(event.getItem()))
            return JmixIcon.EXCLAMATION_TRIANGLE;
        return JmixIcon.HOURGLASS;
    }
}