package kodras_stokic;

import kodras_stokic.CLI.CLIparser;

public class JMSStarter {

	public static void main(String[] args) {

		CLIparser cli = new CLIparser(args);
		cli.parse();
	}
}