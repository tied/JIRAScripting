/**
 * Created by pdwyer on 9/14/2016.
 */


import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.component.ComponentAccessor

def issue = event.issue as Issue
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def tgtField = customFieldManager.getCustomFieldObjects(event.issue).find { it.name == "Release Notes Modified" }
def changeHolder = new DefaultIssueChangeHolder()
def field = event.getChangeLog().getRelated("ChildChangeItem")?.find {it.field == "Release Notes"}
def old_field_value = field.get("oldstring")


if(field) {
    if (old_field_value != null) {
        tgtField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(tgtField), "True"), changeHolder)
    }
}