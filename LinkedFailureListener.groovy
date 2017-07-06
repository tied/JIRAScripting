/**
 * Created by pdwyer on 5/15/2017.
 * Script to send an email to the person that was the one who fixed
 * a linked issue that was a fail
 */


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.mail.Email;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;

// Get all managers we are using
linkMgr = ComponentManager.getInstance().getIssueLinkManager()
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def componentManager = ComponentManager.getInstance()
issueManager = componentManager.getIssueManager()
userManager = ComponentAccessor.getUserManager()

// Debugging stuff
//def issue = componentManager.getIssueManager().getIssueObject("HARVEST-4224")
//(linkMgr.getOutwardLinks(issue.getId())*.issueLinkType.name.contains('Report Failure')) && (issue.status != originalIssue.status)

// Define the constants
def IssueID
def LinkedIssue
def FixedUser
def email
def emails = []
def issues = []
def user

LinkType = "Report Failure"

// For each link check if it is a failure of link
for (IssueLink link in linkMgr.getOutwardLinks(issue.id)) {
    if(link.issueLinkType.name == LinkType) {
        IssueID = link.getDestinationId() // Get the issue id of that link
        if (IssueID > 0) {
            LinkedIssue = issueManager.getIssueObject(IssueID) // Get the issue object
            issues.add(LinkedIssue) // Add that to an array, it will be used later
            FixedUser = LinkedIssue.getCustomFieldValue(customFieldManager.getCustomFieldObjectByName("Fixed By"))
            // Get the fixed by user
            if (FixedUser != null) { // Check if it is filled
                user = userManager.getUser(FixedUser.getName())
                email = user.getEmailAddress()
                // The user should be the username which also should be the username of the email (I hope)
                emails.add(email) // Add the email to the list
            }
        }
    }
}

def subject
def body
def count = 0

// Loop through the email list
for (String address in emails) {
    subject ="The ticket " + issue.toString() + " has been marked as a failure of "+issues[count].toString() // Build the subject
    body = "Failed ticket: https://jira.orchardsoft.com/browse/"+issues[count].toString()+"\n"
    body = body+"Fail reported in:  https://jira.orchardsoft.com/browse/"+issue.toString()
    sendEmail(address,subject,body) // Call the email
    count++
}

// Fuction to build the email and send it
def sendEmail(String emailAddr, String subject, String body) {
    SMTPMailServer mailServer = ComponentManager.getInstance().getMailServerManager().getDefaultSMTPMailServer()
    if (mailServer) { // Check if the email server exists
        Email emailSend = new Email(emailAddr)
        emailSend.setSubject(subject)
        emailSend.setBody(body)
        mailServer.send(emailSend)
    }
}