

/**
 * Created by pdwyer on 10/20/2016.
 */


// Not currently implemented anywhere

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;

import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.issue.comments.CommentManager

userManager = ComponentAccessor.getUserManager()
optionManager = ComponentAccessor.getOptionsManager()

// For testing
def componentManager = ComponentManager.getInstance()
def issue = componentManager.getIssueManager().getIssueObject("HARVEST-4806")

// This method gets all options of a select list
def customFieldManager = componentManager.getCustomFieldManager()
def customField = customFieldManager.getCustomFieldObjectByName("Project Version")
def projectManager = ComponentManager.getInstance().getProjectManager()
def projectCustomManager = customFieldManager.getCustomFieldObjectByName("Project")

//IssueService.IssueInputparameters.addCustomFieldValue('11005','6.0')
def currentOptions = optionManager.getOptions(customField.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig())
def projectName = issue.getCustomFieldValue(projectCustomManager)

def Key
if (projectName=='Harvest'){
    Key = 'HARVEST'
} else if(projectName=='Copia'){
    Key = 'COPIA'
}


// Get the project versions
def project = projectManager.getProjectByCurrentKey(Key)
def options = project.getVersions()

def months = new String[12]
months[0] = 'January'
months[1] = 'Feburary'
months[2] = 'March'
months[3] = 'April'
months[4] = 'May'
months[5] = 'June'
months[6] = 'July'
months[7] = 'August'
months[8] = 'September'
months[9] = 'October'
months[10] = 'November'
months[11] = 'December'

def addValue = true
ArrayList<String> sOptions = new ArrayList<String>()
ArrayList<String> finalList = new ArrayList<String>()


for(String item: currentOptions){
    sOptions.add(item as String)
}

for (String item : options) {
    if (item.contains('Hotfix')) {
        addValue = false
    }
    if (sOptions.contains(item)) {
        addValue = false
    }
    if (addValue) {
        for (String mon : months)
        if (item.contains(mon)) {
            addValue = false
        }
    }

    if(addValue){
        finalList.add(item)
        addOptionToCustomField(customField,item)
    }
    addValue=true
}

return finalList


public Option addOptionToCustomField(CustomField customField, String value) {
    Option newOption = null;
    if (customField != null) {
        List<FieldConfigScheme> schemes = customField.getConfigurationSchemes();
        if (schemes != null && !schemes.isEmpty()) {
            FieldConfigScheme sc = schemes.get(0);
            Map configs = sc.getConfigsByConfig();
            if (configs != null && !configs.isEmpty()) {
                FieldConfig config = (FieldConfig) configs.keySet().iterator().next();
                OptionsManager optionsManager = ComponentAccessor.getOptionsManager();
                Options l = optionsManager.getOptions(config);
                int nextSequence = l.isEmpty() ? 1 : l.getRootOptions().size() + 1;
                newOption = optionsManager.createOption(config, null, (long) nextSequence, value);
            }
        }
    }

    return newOption;
}
