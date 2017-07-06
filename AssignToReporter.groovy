/**
 * Created by pdwyer on 10/30/2016.
 * This will assign the issue to the user who reported it IF they don't manually set it somewhere else
 * This will only work for people in Development (Devs and Testers) and Automation
 * When testing creates a feature request do nothing
 * If testing creates anything else, assign it to the right user
 * Harvest = Tim Spurling
 * Copia = Evan Crawford
 * Interface Engine = John Phillips
 * Labeler = Tim Spurling
 * Orchard Collect = Drew Waddell
 * License Generator = Tim Spurling
 * ODE = David Hardwick
 * Win32API = Seth Leeper
 * Orchard TWAIN = William Ford
 * Product Security = Josh Meek
 * Mapper = Seth Leeper
 *
 * Automation goes to specific people
 * Harvest = Tim Spurling
 * Copia = David Wacker
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.event.type.EventDispatchOption


def componentManager = ComponentManager.getInstance()
userManager = ComponentAccessor.getUserManager()

// For testing
//def issue = componentManager.getIssueManager().getIssueObject("HARVEST-4752")

def userUtil = ComponentAccessor.getUserUtil()
def com.atlassian.crowd.embedded.api.User currentUser =   ComponentManager.instance.jiraAuthenticationContext.getLoggedInUser()
def currentProject = issue.getProjectObject().getName() // Get the Project
def isDevelopment = userUtil.getGroupNamesForUser(currentUser.name).contains("JG_Development") // Check to see if the user is in development
def isTesting = userUtil.getGroupNamesForUser(currentUser.name).contains("JG_Testing") // Check to see if the user is in testing
def isOps = userUtil.getGroupNamesForUser(currentUser.name).contains("JG_Tech_Support")
def isAuto = userUtil.getGroupNamesForUser(currentUser.name).contains("JG_Automation")
def reporterUser = issue.getReporterUser() // Get the reporter
def issueUser = issue.getAssignee() // Get the assignee (should be empty)
def issueType = issue.getIssueType().name // Get the type
def QAUser = userManager.getUser('Quality_Assurance_Te') // Get the QA Team user
def Fixer
def isFeatureRequest
def CurrentProject = issue.getProjectObject().getName() // Get the Project
def isHarvest = false
def isCopia = false

def CopiaQAFixer = userManager.getUser("dwacker") // QA Copia Fixer
def HarvestQAFixer = userManager.getUser("tspurling") // Harvest Fixer

if(CurrentProject == 'Harvest LIS') {
    Fixer = userManager.getUser('tspurling')
    isHarvest = true
} else if(CurrentProject == 'Copia') {
    Fixer = userManager.getUser('ecrawford')
    isCopia=true
} else if(CurrentProject == 'Interface Engine') {
    Fixer = userManager.getUser('jphillips')
} else if(CurrentProject == 'Labeler') {
    Fixer = userManager.getUser('wford')
} else if(CurrentProject == 'License Generator') {
    Fixer = userManager.getUser('tspurling')
} else if(CurrentProject == 'Orchard Collect') {
    Fixer = userManager.getUser('dwaddell')
} else if(CurrentProject == 'Orchard Device Engine') {
    Fixer = userManager.getUser('dhardwick')
} else if(CurrentProject == 'Win32API' || CurrentProject == 'Mapper') {
    Fixer = userManager.getUser('sleeper')
} else if(CurrentProject == 'Orchard TWAIN'){
    Fixer = userManager.getUser('wford')
} else if (CurrentProject == 'Product Security'){
    Fixer = userManager.getUser('jmeek')
}

if(issueType=='Feature Request'){
    isFeatureRequest=true
}

boolean canContinue = (issueUser == null) && (currentProject !='') && (!(isFeatureRequest)) // Error checking
if (canContinue) {
    if (isTesting) {
        issue.setAssignee(Fixer)
    }else if (isAuto){
        if (isHarvest) {
            issue.setAssignee(HarvestQAFixer)
        } else if (isCopia) {
            issue.setAssignee(CopiaQAFixer)
        }

    } else if (isOps) { // Don't do anything with Ops right now, lets keep this in dev for now
        // issue.setAssignee(QAUser)
    } else{
        if(isDevelopment) {
            issue.setAssignee(reporterUser)
        }
    }
    componentManager.getIssueManager().updateIssue(currentUser,issue,EventDispatchOption.ISSUE_UPDATED,false)
}