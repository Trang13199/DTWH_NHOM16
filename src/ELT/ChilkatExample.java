package ELT;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;
import com.mysql.jdbc.PreparedStatement;

public class ChilkatExample {
	Control ct;
	private PreparedStatement ps = null;
	private String sql;

	static {
		try {
			System.loadLibrary("chilkat");// copy file chilkat.dll vao thu muc project
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	public void getTrial() {
		CkGlobal glob = new CkGlobal();
		boolean success = glob.UnlockBundle("Anything for 30-day trial");
		if (success != true) {
			System.out.println(glob.lastErrorText());
			return;
		}
		int status = glob.get_UnlockStatus();
		if (status == 2) {
			System.out.println("Unlocked using purchased unlock code.");
		} else {
			System.out.println("Uncloked in trail mode.");
		}
		System.out.println(glob.lastErrorText());
	}

	public void downloadFile(String host, int ports, String user, String pass, String path, String local)
			throws ClassNotFoundException, SQLException {
		CkSsh ssh = new CkSsh();
		// Hostname may be an IP address or hostname:
//		String hostname = "www.some-ssh-server.com";
//		String hostname = "http://drive.ecepvn.org:5000/index.cgi?launchApp=SYNO.SDS.App.FileStation3.Instance&launchParam=openfile%3D%252FECEP%252Fsong.nguyen%252FDW_2020%252F&fbclid=IwAR1GjbMt_ZWTairglWCjOQQH6Q0NbyXgl0qP7LTBahWmR4HcJXNVoh5o5fw";
//		String hostname = "drive.ecepvn.org";
//		System.out.println(c.getHost());
		String hostname = host;

//		int port = 2227;
		int port = ports;

		// Connect to an SSH server:
		boolean success = ssh.Connect(hostname, port);
		if (success != true) {
			System.out.println(ssh.lastErrorText());
			return;
		}

		// Wait a max of 5 seconds when reading responses..
		ssh.put_IdleTimeoutMs(5000);

		// Authenticate using login/password:
		success = ssh.AuthenticatePw(user, pass);
		if (success != true) {
			System.out.println(ssh.lastErrorText());
			return;
		}

		// Once the SSH object is connected and authenticated, we use it
		// in our SCP object.
		CkScp scp = new CkScp();

		success = scp.UseSsh(ssh);
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}

//		scp.put_SyncMustMatch(c.getTableName());// down tat ca cac file bat dau bang sinhvien
		String remotePath = path;
		String localPath = local; // thu muc muon down file ve
		success = scp.SyncTreeDownload(remotePath, localPath, 2, false);

		/*
		 * String remotePath =
		 * "/volume1/ECEP/song.nguyen/DW_2020/data/17130276_Sang_Nhom4.xlsx"; // String
		 * localPath = "/home/bob/test.txt"; String localPath =
		 * "E:\\DATA WAREHOUSE\\Error\\17130276_Sang_Nhom4.xlsx"; success =
		 * scp.DownloadFile(remotePath, localPath);
		 */
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}

		System.out.println("SCP download file success.");

		// Disconnect
		ssh.Disconnect();

	}

	public void getLog(String target_table, String dbLog) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String timestamp = dtf.format(now);
		sql = "INSERT INTO " + dbLog
				+ " (file_name, data_file_config_id,file_status,staging_load_count,file_timestamp) VALUES (?,?,?,?,?)";

		try {
			ps = (PreparedStatement) ConnectionDB.getConnection("dbcontrol").prepareStatement(sql);
			ps.setString(1, target_table);
			ps.setInt(2, 1);
			ps.setString(3, "ER");
			ps.setInt(4, 20);
			ps.setString(5, timestamp);
			ps.executeUpdate();
			System.out.println("Success insert to log");
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
	}

}
