import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager

userManager = ComponentAccessor.getUserManager()
ComponentManager componentManager = ComponentManager.getInstance()

def devUser = userManager.getUser('Testing_Team')
def fixedByUser = userManager.getUser(issue.getCustomFieldValue(componentManager.getCustomFieldManager().getCustomFieldObjectByName("Fixed By")).name)

if(fixedByUser != null) { //If we're not dealing with an Interface Engine issue, then assign to the last tester who looked at this issue.
    issue.setAssignee(fixedByUser)
}
else { //Otherwise, assign to the Tester user.
    issue.setAssignee(devUser)
}