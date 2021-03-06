
//import com.atlassian.jira.component.ComponentAccessor
//import com.atlassian.jira.ComponentManager
//import com.atlassian.jira.bc.issue.IssueService
//import com.atlassian.jira.issue.MutableIssue
//import com.atlassian.crowd.embedded.api.User
//import com.atlassian.jira.workflow.JiraWorkflow
//import com.opensymphony.workflow.loader.ActionDescriptor
//import com.opensymphony.workflow.loader.WorkflowDescriptor
//import com.atlassian.jira.issue.IssueInputParameters
//import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult
//import com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult
//import com.sun.org.apache.xpath.internal.functions.FuncFalse

/**
 * Created by pdwyer on 10/20/2016.
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.issue.comments.CommentManager



// For testing
def componentManager = ComponentManager.getInstance()
CommentManager commentManager = ComponentAccessor.getCommentManager()
if(issue == null){
    def issue = componentManager.getIssueManager().getIssueObject("HARVEST-4748")
}
userManager = ComponentAccessor.getUserManager()
def issueService = ComponentAccessor.getIssueService()
def userUtil = ComponentAccessor.getUserUtil()

def status = issue.getStatus()
def currentProject = issue.getProjectObject().getName() // Get the Project
def issueType = issue.issueTypeObject.name

def tempUser = ComponentAccessor.getJiraAuthenticationContext().getUser() // Old way to get user, embedded api user is needed for transition
def com.atlassian.crowd.embedded.api.User currentUser =   ComponentManager.instance.jiraAuthenticationContext.getLoggedInUser()
def isSupport = userUtil.getGroupNamesForUser(currentUser.name).contains("JG_Tech_Support") // Check to see if the user is in support
def reporterUser = issue.getReporterUser()
def issueUser = issue.getAssignee()


// Get workflow stuff, workflow object, the action being done, and the input parameters
JiraWorkflow jiraWorkflow = componentManager.getWorkflowManager().getWorkflow(issue)
def action = jiraWorkflow.getLinkedStep(status).getActions()
IssueInputParameters issueInputParameters = issueService.newIssueInputParameters() // These will always be blank, we just need the

if(currentProject != '') {
    if(issueUser == null){
        issue.setAssignee(reporterUser)
        componentManager.getIssueManager().updateIssue(currentUser,issue,EventDispatchOption.ISSUE_UPDATED,false)
    }
}

def isToDo = true

if(issueType == "Feature Request"){
    isToDo = false
}

if (isToDo) {
    // Find the To Operations action ID if it exists as a linked transition
    count = 0
    int ActionId = 0
    for (String item : action) {
        if (item.contains('To Operations')){
            commentManager.create(issue,tempUser,"Found To Ops "+item,false)
            ActionId = action.get(count).id
        }
        count += 1
    }

    if(ActionId > 0){
        commentManager.create(issue,tempUser,"Have Action ID "+ActionId,false)
        def IssueService.TransitionValidationResult validationResult = issueService.validateTransition (currentUser,issue.id,ActionId, issueInputParameters)
        def errorCollection = validationResult.errorCollection
        if(! errorCollection.hasAnyErrors()) {
            commentManager.create(issue,tempUser,"No errors",false)
            IssueResult issueResult = issueService.transition(currentUser, validationResult)
            issue.setStatusObject(issueResult.getIssue().getStatusObject())

        } else {
            commentManager.create(issue,tempUser,"Errors",false)
            commentManager.create(issue,tempUser,""+errorCollection.getErrorMessages(),false)
            def action2 = jiraWorkflow.getLinkedStep(status).getActions()
            commentManager.create(issue,tempUser,""+action2.get(1),false)
            commentManager.create(issue,tempUser,""+action2.get(1).id,false)
        }
    }
}

ComponentAccessor.getIssueIndexManager().reIndex(issue)
componentManager.getIssueManager().updateIssue(currentUser,issue,EventDispatchOption.ISSUE_UPDATED,false)