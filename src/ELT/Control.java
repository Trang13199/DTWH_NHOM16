package ELT;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Control {
	private String db_control;
	private String db_staging;
	private String db_warehouse;
	private String table_config;
	private String table_log;

	public String getTable_log() {
		return table_log;
	}

	public void setTable_log(String table_log) {
		this.table_log = table_log;
	}

	public String getDb_control() {
		return db_control;
	}

	public void setDb_control(String db_control) {
		this.db_control = db_control;
	}

	public String getDb_staging() {
		return db_staging;
	}

	public void setDb_staging(String db_staging) {
		this.db_staging = db_staging;
	}

	public String getDb_warehouse() {
		return db_warehouse;
	}

	public void setDb_warehouse(String db_warehouse) {
		this.db_warehouse = db_warehouse;
	}

	public String getTable_config() {
		return table_config;
	}

	public void setTable_config(String table_config) {
		this.table_config = table_config;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException, EncryptedDocumentException,
			InvalidFormatException, IOException {
		Config cf = new Config();
		Control c = new Control();
		Log l = new Log();
		c.setDb_staging("staging");
		c.setDb_control("dbcontrol");
		c.setTable_config("configuration");
		c.setTable_log("log");
		cf.setC(c);
		cf.getConfig(2, "|");
		while (true) {
			System.out.println("Choose a step:\n 1. GetConnection\n 2. Download file\n 3. Load Staging\n 0. Exist");
			Scanner sc = new Scanner(System.in);
			int value = sc.nextInt();
			if (value == 1) {
				Connection conn = new ConnectionDB().getConnection("staging");
				Connection con = new ConnectionDB().getConnection(c.getDb_control());

				if (conn != null) {
					System.out.println("Thanh cong");
				}
			} else if (value == 2) {
				// download
				System.out.println("DOWNLOAD FILE");
				Scanner sc2 = new Scanner(System.in);
				ChilkatExample.getLog(c.table_log, c.db_control, c.table_config);
			} else if (value == 3) {
				// staging
				System.out.println("LOAD STAGING");
				Scanner sc2 = new Scanner(System.in);
				cf.extractData();
				cf.updateLog("ER", cf.target_table);

			} else if (value == 0) {
				System.out.println("Good bye");
				break;
			} else {
				System.out.println("NOT FOUND");
			}
		}
	}
}
