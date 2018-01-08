package com.gree.air.condition.sms;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import com.gree.air.condition.constant.Constant;

/**
 * 短信服务
 * 
 * @author lihaotian
 *
 */
public class SmsServer implements Runnable {

	private MessageConnection msgconn;
	private Message message;
	private static String Sms_Address = "";

	/**
	 * 启动短信服务
	 */
	public static void startServer() {

		new Thread(new SmsServer()).start();
	}

	public void run() {

		try {

			String address = "sms://:0";
			msgconn = (MessageConnection) Connector.open(address);

			while (true) {

				/* gets message object */
				message = msgconn.receive();

				if (message != null) {

					/* gets address of the message received */
					Sms_Address = message.getAddress();

					/* check for the type of message received */
					if (message instanceof TextMessage) {

						// System.out.println("Recieved Text Messsage: ===== " + ((TextMessage)
						// message).getPayloadText());

						Constant.Sms_Receive = ((TextMessage) message).getPayloadText();
						SmsModel.analyze();

					} else if (message instanceof BinaryMessage) {

						/* get the binary data of the message */
						// byte[] data = ((BinaryMessage) message).getPayloadData();
						// System.out.println("Recieved Binary Messsage: \n" + new String(data));

						Constant.Sms_Receive = new String(((BinaryMessage) message).getPayloadData());
						SmsModel.analyze();
					}

				}
			}

		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	/**
	 * 发送短信
	 * 
	 * @param message
	 */
	public static void sendMessage() {

		new Thread(new Runnable() {
			public void run() {

				try {

					/* to open message connection */
					MessageConnection msgconnSend = (MessageConnection) Connector.open(Sms_Address);

					// sending text message
					TextMessage textmsg = (TextMessage) msgconnSend.newMessage(MessageConnection.TEXT_MESSAGE);

					/* pay load text passed here */
					textmsg.setPayloadText(Constant.Sms_Send);
					/* send the text message */
					msgconnSend.send(textmsg);

					msgconnSend.close();

				} catch (IOException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
	}

}
