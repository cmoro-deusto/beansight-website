package notifiers;
 
import play.*;
import play.mvc.*;
import play.i18n.*;
import java.util.*;

import models.MailConfirmTask;

public class Mails extends Mailer {


   public static void confirmation(MailConfirmTask task) {
	  Logger.info("Confirmation email: sending to " + task.to.email);
	   
      setSubject(Messages.get("emailconfirmationsubject"));
      addRecipient(task.to.email);
      setFrom("contact@beansight.com");
      
      send(task);
   }
   
}