package org.coody.framework.mail.sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import org.coody.framework.core.logger.BaseLogger;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.mail.entity.EmailSendConfig;
import org.coody.framework.mail.exception.MailException;

public class EmailSender {

	static BaseLogger logger = BaseLogger.getLogger(EmailSender.class);

	private EmailSendConfig emailSendConfig;

	private Socket socket;

	private BufferedReader bufferedReader = null;

	private OutputStream outputStream = null;

	public EmailSender(EmailSendConfig emailSendConfig) {
		this.emailSendConfig = emailSendConfig;
		init();
	}

	private synchronized void init() {
		try {
			if (emailSendConfig.getPort() == 25) {
				socket = new Socket(emailSendConfig.getSmtp(), emailSendConfig.getPort());
			} else {
				socket = SSLSocketFactory.getDefault().createSocket(emailSendConfig.getSmtp(),
						emailSendConfig.getPort());
			}
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
			outputStream = socket.getOutputStream();

			String line = readLine();
			if (!line.startsWith("220")) {
				throw new MailException("邮件服务器初始化失败:" + line);
			}
			String esmtp = StringUtil.textCutCenter(line, " ", " ");
			sendAndReadLine("HELO " + esmtp + "\r\n");
			sendAndReadLine("AUTH LOGIN " + "\r\n");
			sendAndReadLine(Base64.getEncoder().encodeToString(emailSendConfig.getEmail().getBytes()) + "\r\n");
			line = sendAndReadLine(
					Base64.getEncoder().encodeToString(emailSendConfig.getPassword().getBytes()) + "\r\n");
			if (!line.startsWith("235")) {
				throw new MailException("邮件服务器初始化失败:" + line);
			}
		} catch (MailException e) {
			throw e;
		} catch (Exception e) {
			throw new MailException("邮件服务器初始化失败", e);
		}
	}

	public void send(List<String> targeEmail, String subject, String content) {

		try {
			String temp = sendAndReadLine("MAIL FROM:<" + emailSendConfig.getEmail() + ">" + "\r\n");
			logger.info(temp);
			for (String email : targeEmail) {
				sendAndReadLine("RCPT TO:<" + email + ">" + "\r\n");
			}
			sendAndReadLine("DATA " + "\r\n");
			StringBuilder builder = new StringBuilder();
			builder.append("From:<" + emailSendConfig.getEmail() + ">" + "\r\n");

			for (String email : targeEmail) {
				builder.append("To:<" + email + ">" + "\r\n");
			}
			builder.append("Subject:" + subject + "\r\n");
			builder.append("Date:" + Calendar.getInstance().getTime().toString() + "\r\n");
			builder.append("Content-Type:text/plain;charset=\"UTF-8\"" + "\r\n");
			builder.append("\r\n");
			builder.append(content);
			builder.append("\r\n" + "." + "\r\n");
			sendAndReadLine(builder.toString());
			readLine();
			readLine();
			readLine();
			readLine();

		} catch (MailException e) {
			throw e;
		} catch (Exception e) {
			throw new MailException("发送邮件失败", e);
		}
	}

	public void send(String targeEmail, String subject, String content) {
		send(Arrays.asList(targeEmail), subject, content);
	}

	private String sendAndReadLine(String Command) throws IOException {
		outputStream.write(Command.getBytes());
		outputStream.flush();
		return readLine();
	}

	private String readLine() throws IOException {
		String line = bufferedReader.readLine();
		logger.info(line);
		System.out.println(line);
		if (StringUtil.isNullOrEmpty(line)) {
			throw new MailException("连接邮件服务器失败");
		}
		if (!line.startsWith("2") && !line.startsWith("3")) {
			throw new MailException(line);
		}
		return line;
	}

}
