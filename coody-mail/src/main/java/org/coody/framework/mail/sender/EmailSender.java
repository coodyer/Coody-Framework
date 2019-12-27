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

	private EmailSendConfig sendEntity;

	private Socket socket;

	private BufferedReader bufferedReader = null;

	private OutputStream outputStream = null;

	public EmailSender(EmailSendConfig sendEntity) {
		this.sendEntity = sendEntity;
		init();
	}

	private synchronized void init() {
		try {
			if (sendEntity.getPort() == 25) {
				socket = new Socket(sendEntity.getSmtp(), sendEntity.getPort());
			} else {
				socket = SSLSocketFactory.getDefault().createSocket(sendEntity.getSmtp(), sendEntity.getPort());
			}
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
			outputStream = socket.getOutputStream();

			readLine();
			sendAndReadLine("HELO " + sendEntity.getSmtp() + "\r\n");
			sendAndReadLine("STARTTLS " + sendEntity.getSmtp() + "\r\n");
			sendAndReadLine("AUTH LOGIN " + "\r\n");
			sendAndReadLine(Base64.getEncoder().encodeToString(sendEntity.getEmail().getBytes()) + "\r\n");
			sendAndReadLine(Base64.getEncoder().encodeToString(sendEntity.getPassword().getBytes()) + "\r\n");
		} catch (MailException e) {
			throw e;
		} catch (Exception e) {
			throw new MailException("邮件服务器初始化失败", e);
		}
	}

	public void send(List<String> targeEmail, String subject, String content) {
		try {
			String temp = sendAndReadLine("MAIL FROM:<" + sendEntity.getEmail() + ">" + "\r\n");
			logger.info(temp);
			for (String email : targeEmail) {
				sendAndReadLine("RCPT TO:<" + email + ">" + "\r\n");
			}
			sendAndReadLine("DATA " + "\r\n");
			StringBuilder builder = new StringBuilder();
			builder.append("From:<" + sendEntity.getEmail() + ">" + "\r\n");

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
		// flush冲一下
		outputStream.flush();
		return readLine();
	}

	private String readLine() throws IOException {
		String line = bufferedReader.readLine();
		logger.info(line);
		if (StringUtil.isNullOrEmpty(line)) {
			throw new MailException("连接邮件服务器失败");
		}
		if (!line.startsWith("2") && !line.startsWith("3")) {
			throw new MailException(line);
		}
		return line;
	}

}
