package config;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtility {

	private static final String SENDER_GMAIL = "beruntungtech.my@gmail.com";
	private static final String APP_PASSWORD = "ueis rlie bgrh qxqa";

	public static void sendNotification(String recipientEmail, String subject, String htmlBody) {

		new Thread(() -> {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(SENDER_GMAIL, APP_PASSWORD);
				}
			});

			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(SENDER_GMAIL, "UniVerse Campus Marketplace"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
				message.setSubject(subject);

				message.setContent(htmlBody, "text/html; charset=utf-8");

				Transport.send(message);
				System.out.println("LOG: Notification Email dispatched successfully to " + recipientEmail);
			} catch (Exception e) {
				System.err.println("ERROR: Failed to transmit background notification email.");
				e.printStackTrace();
			}
		}).start();
	}
}