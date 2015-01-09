package kodras_stokic.CLI;

import kodras_stokic.JMS.User;

import org.apache.commons.cli.*;

/**
 * Kommandozeilen Argumente werden im GNU Style geparst.
 * 
 * @author Stokic Stefan
 * @version 1.1
 */
public class CLIparser {

	private String[] args;
	private Options options;

	public CLIparser(String[] args){

		this.args = args;
		this.options = new Options();

		Option server = OptionBuilder.withArgName("server")
				.hasArg()
				.withDescription("IP-Adresse des Brokers")
				.create("s");
		Option user = OptionBuilder.withArgName("user")
				.hasArg()
				.withDescription("Der zu verwendende Username")
				.create("u");
		Option chatroom = OptionBuilder.withArgName("chatroom")
				.hasArg()
				.withDescription("Der Name des Chatrooms")
				.create("cr");

		this.options.addOption(server);
		this.options.addOption(user);
		this.options.addOption(chatroom);
	}

	/**
	 * Methode die die Argumente parst und wenn sie korrekt sind, wird Sekretariat ausgeführt
	 */
	public void parse(){

		GnuParser parser = new GnuParser();

		try{

			CommandLine line = parser.parse(this.options, this.args);

			if(line.hasOption("s") && line.hasOption("u") && line.hasOption("cr")){

				String serverVal = line.getOptionValue("s");
				String userVal = line.getOptionValue("u");
				String chatroomVal = line.getOptionValue("cr");
				
				if(!serverVal.isEmpty() && !userVal.isEmpty() && !chatroomVal.isEmpty())
					new User(serverVal, userVal, chatroomVal).start();
				else
					this.help();

			}else{

				this.help();
			}

		}catch(ParseException e){

			this.help();
		}
	}

	/**
	 * Methode die eine Hilfestellung zeigt
	 */
	protected void help(){

		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("JMS Chat", this.options);
	}
}
