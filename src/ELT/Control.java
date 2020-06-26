package ELT;

import java.io.IOException;
import java.sql.SQLException;

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
		ChilkatExample gg = new ChilkatExample();
		System.out.println(c.db_control);
		gg.getTrial();
		gg.downloadFile(cf.host, cf.port, cf.userRemote, cf.passRemote, cf.remotePath, cf.success_fir);
		gg.getLog(cf.target_table, c.table_log);
		cf.extractData();
		cf.updateLog("ER", cf.target_table);
	}
}
