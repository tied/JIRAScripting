/**
 * Created by pdwyer on 9/14/2016.
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult
import com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult

//changeItems.any {
//    (it.field == 'Release Notes') && (it.oldstring != it.newstring) && (it.oldstring != null)
//}

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue

Issue issue = event.issue
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def change = (it.field == 'Release Notes') && (it.oldstring != it.newstring) && (it.oldstring != null)
if (change) {
    def customField = customFieldManager.getCustomFieldObjectByName('Release Notes Edited')
    issue.setCustomFieldValue(customField,"Edited")
} else {
    return
}


//
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.component.ComponentAccessor
def issue = event.issue as Issue
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def tgtField = customFieldManager.getCustomFieldObjects(event.issue).find { it.name == "Release Notes Edited" }
def changeHolder = new DefaultIssueChangeHolder()
def change = event?.getChangeLog()?.getRelated("ChildChangeItem")?.find {it.field == "Release Notes"}

if(change) {
    tgtField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(tgtField), "Edited"), changeHolder)
}

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.component.ComponentAccessor
def issue = event.issue as Issue
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def tgtField = customFieldManager.getCustomFieldObjects(event.issue).find { it.name == "Release Notes Edited" }
def changeHolder = new DefaultIssueChangeHolder()
def field = event.getChangeLog().getRelated("ChildChangeItem")?.find {it.field == "Release Notes"}
def old_field_value = field.oldstring


if(field) {
    if (old_field_value != null) {
        tgtField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(tgtField), "Edited"), changeHolder)
    }
}