/**
 * Created by PRD on 9/14/2016.
 * Will be used for the Fix event for the following projects
 * Harvest
 * Copia
 * Interface Engine
 * Product Security
 * Labeler
 * License Generator
 * Orchard Collect
 * ODE
 * Trellis
 * Win32API
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.ComponentManager

userManager = ComponentAccessor.getUserManager()
// For testing
//def componentManager = ComponentManager.getInstance()
//def issue = componentManager.getIssueManager().getIssueObject("HARVEST-4806")

def userUtil = ComponentAccessor.getUserUtil()
def assignToUser = userManager.getUser('kyoung')
def testerUser = userManager.getUser('Testing_Team')
def ODEUser = userManager.getUser('ODE_Team')
def PSUser = userManager.getUser('jmeek')
def testedByUser
def CurrentProject = issue.getProjectObject().getName()
def reporterUser = issue.getReporterUser() // Get the reporter
def isTestingReporter = userUtil.getGroupNamesForUser(reporterUser.getName()).contains("JG_Testing") // Check to see if the user is in testing


if(issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("Tested By")) != null) {
    testedByUser = userManager.getUser(issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("Tested By")).name)
}
else {
    testedByUser = null
}

def containsLabel = "FSR" in issue.getLabels()*.label // If the label contains FSR then it will need to go to Zac first

if (issue.components*.name.contains('Interface Engine') || CurrentProject == 'Interface Engine') {
    //Check override for Interface Engine fixes.  If the component is Interface Engine, assign to Kevin Young.
    issue.setAssignee(assignToUser)
} else if (testedByUser != null) {
    //If we're not dealing with an Interface Engine issue, then assign to the last tester who looked at this issue.
    issue.setAssignee(testedByUser)
} else if (CurrentProject == 'Product Security' || CurrentProject == 'Win32API' | containsLabel) {
    issue.setAssignee(PSUser)
}else if(isTestingReporter){ // If a tester reported this, send it back to that same tester
    issue.setAssignee(reporterUser)
} else { //Otherwise, assign to the Tester user.
    issue.setAssignee(testerUser)
}
