/**
 * Created by PRD on 9/14/2016
 * This script will fill in the assignment field for every product verification
 * Currently the assignments are the following:
 * Harvest = Jeanne Merry
 * Copia = Bodie Shallenberger
 * Interface Engine = Megan Stanage
 * Product Security = Josh Meek
 * Labeler = Katherine Wertz
 * License Generator = Katherine Wertz
 * Orchard Collect = Bodie Shallenberger
 * ODE = Karen Hockins
 * Trellis = Megan Stanage
 * Win32API = Katherine Wertz
 * TWAIN = Megan Stanage
 * Mapper = Seth Leeper
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager

userManager = ComponentAccessor.getUserManager()

// For testing
//def componentManager = ComponentManager.getInstance()
//def issue = componentManager.getIssueManager().getIssueObject("IE-10")

def HarvestVerified = userManager.getUser('jmerry') // Harvest
def SequoiaVerified = userManager.getUser('khockins') // ODE/Trellis Enterprise/Sequoia
def CopiaVerified = userManager.getUser('bshallenberger') // Copia
def OtherVerified = userManager.getUser('kwertz') // Labeler/License Generator/Win32API
def PSVerified = userManager.getUser('jmeek') // Product Security
def TrellisVerified =  userManager.getUser('mstanage') // Trellis
def MapperVerified = userManager.getUser('sleeper') // Mapper
def CurrentProject = issue.getProjectObject().getName() // Get the Project

def containsLabel = "FSR" in issue.getLabels()*.label // If the label contains FSR then it will need to go to Zac first

// Depending on the project set the assignee; this can be added to and changed if need be
if (CurrentProject == 'Product Security' || CurrentProject == 'Win32API' | containsLabel) { // Goes to Zac if it has an FSR label or is PS/Win32API
    issue.setAssignee(PSVerified)
} else if (CurrentProject == 'Harvest LIS') {
    issue.setAssignee(HarvestVerified)
} else if (CurrentProject == 'Copia' || CurrentProject == 'Orchard Collect') {
    issue.setAssignee(CopiaVerified)
} else if (CurrentProject == 'Orchard Device Engine') {
    issue.setAssignee(SequoiaVerified)
} else if (CurrentProject == 'Trellis' || CurrentProject == 'Interface Engine' || CurrentProject == 'Orchard TWAIN') {
    issue.setAssignee(TrellisVerified)
} else if (CurrentProject == 'Mapper'){
    issue.setAssignee(MapperVerified)
} else {
    issue.setAssignee(OtherVerified)
}