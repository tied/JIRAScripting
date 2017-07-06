package com.onresolve.jira.groovy.listeners

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.workflow.JiraWorkflow
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.event.IssueCreatedEvent


class CreateListener extends AbstractIssueEventListener {
    Category log = Category.getInstance(CreateListener.class)

    @Override
    void workflowEvent(IssueEvent event) {

// For testing
            def componentManager = ComponentManager.getInstance()
            MutableIssue issue = event.issue

            userManager = ComponentAccessor.getUserManager()
            def issueService = ComponentAccessor.getIssueService()

            def status = issue.getStatus()
            def currentProject = issue.getProjectObject().getName() // Get the Project
            def issueType = issue.issueTypeObject.name

//def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser() // Old way to get user, embedded api user is needed for transition
            def com.atlassian.crowd.embedded.api.User currentUser =   ComponentManager.instance.jiraAuthenticationContext.getLoggedInUser()


// Get workflow stuff, workflow object, the action being done, and the input parameters
            JiraWorkflow jiraWorkflow = componentManager.getWorkflowManager().getWorkflow(issue)
            def action = jiraWorkflow.getLinkedStep(status).getActions()
            IssueInputParameters issueInputParameters = issueService.newIssueInputParameters() // These will always be blank, we just need the


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
                        ActionId = action.get(count).id
                    }
                    count += 1
                }

                if(ActionId > 0){
                    def IssueService.TransitionValidationResult validationResult = issueService.validateTransition (currentUser,issue.id,ActionId, issueInputParameters)
                    def errorCollection = validationResult.errorCollection
                    if(! errorCollection.hasAnyErrors()) {
                        IssueResult issueResult = issueService.transition(currentUser, validationResult)
                        issue.setStatusObject(issueResult.getIssue().getStatusObject())
                    }
                }
            }

            ComponentAccessor.getIssueIndexManager().reIndex(issue)
            componentManager.getIssueManager().updateIssue(currentUser,issue,EventDispatchOption.ISSUE_UPDATED,false)
        }
}