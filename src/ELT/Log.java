package ELT;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

public class Log {
	String file_name;
	int data_file_config_id;
	String file_status;
	String staging_load_count;
	String file_timestamp;
	PreparedStatement pre;
	Control c;

	public void getLog() throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM " + c.getDb_control()+"."+ c.getTable_log();
		pre = (PreparedStatement) ConnectionDB.getConnection(c.getDb_control()).prepareStatement(sql);
		ResultSet rs = pre.executeQuery();
		rs.next();
		file_name = rs.getString("file_name");
		data_file_config_id = rs.getInt("data_file_config_id");
		file_status = rs.getString("file_status");
		file_timestamp = rs.getString("file_timestamp");
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public int getData_file_config_id() {
		return data_file_config_id;
	}

	public void setData_file_config_id(int data_file_config_id) {
		this.data_file_config_id = data_file_config_id;
	}

	public String getFile_status() {
		return file_status;
	}

	public void setFile_status(String file_status) {
		this.file_status = file_status;
	}

	public String getStaging_load_count() {
		return staging_load_count;
	}

	public void setStaging_load_count(String staging_load_count) {
		this.staging_load_count = staging_load_count;
	}

	public String getFile_timestamp() {
		return file_timestamp;
	}

	public void setFile_timestamp(String file_timestamp) {
		this.file_timestamp = file_timestamp;
	}

}
