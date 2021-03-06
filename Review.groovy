/**
 * Created by pdwyer on 10/30/2016.
 * Review script that will find the last person that made the review transition and
 * then set the assignee to that person IF the assignee is set to the current user.
 * So person A assigns it to person B for review, then person B completes their review
 * and does not change the default assignee (which would person B) it will then be
 * automatically reassigned to person A.
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager

// For testing
def componentManager = ComponentManager.getInstance()
//def issue = componentManager.getIssueManager().getIssueObject("TRELLIS-38")


userManager = ComponentAccessor.getUserManager()

// Get the current user and wo it is supposed to be assigned to
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser().getName()
def assignee = issue.getAssignee().getName()

// Get the change history manager and all the changes
def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def allChangeList = changeHistoryManager.getAllChangeItems(issue)

// go through the changes backwards and find the last instance of the status changing to review
def length = allChangeList.size()
for (i = length-1; i>=0; i--) {
    if(allChangeList[i].getField() == 'status'){
        if(allChangeList[i].getToValue() == '10101'){
            assignTo = allChangeList[i+1].getFromValue() // Get the next one, should always be the assignee if they assigned it in the review transition
            i=0 // Kill the loop
        }
    }
}

if(assignTo != '') { // Some error checking
    def assignToUser = userManager.getUser(assignTo)  // The value returned is a string, use it to find the user
    if (assignToUser != null) {
        if (assignee == currentUser) { // Only change it if the assignee is the current user still
        issue.setAssignee(assignToUser)
        }
}
}


