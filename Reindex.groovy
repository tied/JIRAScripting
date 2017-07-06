/**
 * Created by PRD on 10/28/2016
 * Reindexes an issue
 **/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.event.type.EventDispatchOption

def com.atlassian.crowd.embedded.api.User currentUser =   ComponentManager.instance.jiraAuthenticationContext.getLoggedInUser() // Get the user for the update issue command

// Just reindex the issue and send the update
ComponentAccessor.getIssueIndexManager().reIndex(issue)
ComponentManager.getInstance().getIssueManager().updateIssue(currentUser,issue,EventDispatchOption.ISSUE_UPDATED,false)