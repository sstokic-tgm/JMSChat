package kodras_stokic.JMS;

public class MessageChecker {

	public MessageType checkMessage(String input) {
		
		if(input.isEmpty())
			return MessageType.EMPTY;
		
		String[] splittedInput = input.split(" ");
		
		switch(splittedInput[0]) {
		
		case "MAIL":
			
			return splittedInput.length >= 3 ? MessageType.MAIL_SEND : MessageType.MAIL_ERROR;
			
		case "MAILBOX":
			
			return splittedInput.length == 1 ? MessageType.MAILBOX : MessageType.MAILBOX_ERROR;
			
		case "EXIT":
			
			return MessageType.EXIT;
		}
		
		return MessageType.MESSAGE_SEND;
	}
}