/**
 * Created by pdwyer on 9/14/2016.
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult
import com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult

userManager = ComponentAccessor.getUserManager()

// For testing
def componentManager = ComponentManager.getInstance()
def issue = componentManager.getIssueManager().getIssueObject("HARVEST-4685")
def workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue)
def wfd = workflow.getDescriptor()
def actionName = wfd.getAction(transientVars["actionId"] as int).getName()

def assignToUser = userManager.getUser('kyoung')
def testerUser = userManager.getUser('Testing_Team')
def transitionUser = userManager.getUser('pdwyer')
def reporterUser = issue.getReporterUser()
def status = issue.getStatus()
JiraWorkflow jiraWorkflow = workflowManager.getWorkflow(issue)
def action = jiraWorkflow.getLinkedStep(status).getActions()

if (status == 'Committee') {
    def validationResult = IssueService.validateTransition (transitionUser,issue,)
}



count = 0
for (String item : action) {
    if (item.contains('To Operations')){
        ActionId = action.get(count).id
    }
    count += 1
}
a

// Listener for when the Release notes are edited.
changeItems.any {
    (it.field == 'Release Notes') && (it.oldstring != it.newstring) && (it.oldstring != null)
}
changeItems.any {
    (it.field == 'Fix Notes') && (it.oldstring != it.newstring) && (it.oldstring != null)
}