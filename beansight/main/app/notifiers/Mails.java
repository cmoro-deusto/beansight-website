package notifiers;

import play.*;
import play.mvc.*;
import play.i18n.*;
import play.Logger;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import models.CommentNotificationMailTask;
import models.ContactMailTask;
import models.FollowNotificationTask;
import models.Insight;
import models.InvitationMailTask;
import models.MailTask;
import models.MessageMailTask;
import models.User;

public class Mails extends Mailer {

	public static void confirmation(User user) {
		Logger.info("Confirmation email: sending to " + user.email);
		Lang.set(user.uiLanguage.label);
		setSubject(Messages.get("emailconfirmationsubject"));
		addRecipient(user.email);
		setFrom("notification@beansight.com");

		send(user);
	}

	public static boolean followNotification(FollowNotificationTask task) {
		Lang.set(task.language);
		return sendMailTask(task, Messages.get("emailfollownotificationsubject", task.follower.userName), "Mails/followNotification.html");
	}

	public static boolean invitation(InvitationMailTask task) {
		Lang.set(task.language);
		return sendMailTask(task, Messages.get("emailinvitationsubject", task.invitation.invitor.userName), "Mails/invitation.html");
	}
	
	public static boolean message(MessageMailTask task) {
		Lang.set(task.language);
		return sendMailTask(task, Messages.get("emailmessagesubject", task.message.fromUser.userName), "Mails/message.html");
	}
	
	// FIXME : changer le template pour contact
	public static boolean contact(ContactMailTask task) {
		Lang.set(task.language);
		return sendMailTask(task, task.subject, "Mails/contact.html");
	}
	
	private static boolean sendMailTask(MailTask task, String subject, String templateName) {
		Lang.set(task.language);
		
		task.attempt++;
		task.save();
		Logger.info("MailTask " + task.getClass().getSimpleName() + " to: " + task.sendTo);

		setSubject(subject);
		addRecipient(task.sendTo);
		setFrom("notification@beansight.com");
		
		Lang.set(task.language);	

		try {
			return send(templateName, task).get(20, TimeUnit.SECONDS);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} 
	}
	
	
	public static void forgotPassword(String forgotPasswordId, String email, String templateName, String language) {
		Lang.set(language);	
		setSubject(Messages.get("forgotPassword.subject"));
		addRecipient(email);
		setFrom("notification@beansight.com");

		send(templateName, forgotPasswordId);
	}
	
	public static boolean commentNotification(CommentNotificationMailTask task) {
		Lang.set(task.language);	
		User commentWriter = task.commentNotificationMsg.fromUser;
		
		return sendMailTask(task, Messages.get("commentNotificationMail.subject", commentWriter.userName), "Mails/commentNotification.html");
	}
	
}
