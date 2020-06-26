package ELT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Config {
	private PreparedStatement pre = null;
	String listofcol;
	ArrayList<String> columnsList = new ArrayList<String>();
	ArrayList<Cell> valuesList = new ArrayList<Cell>();
	Iterator<Row> itr;
	Iterator<Cell> cellIterator;
	int numOfcol;
	int numOfdata;
	String config_name;
	String target_table;
	String file_type;
	String import_dir;
	String success_fir;
	String error_dir;
	String column_list;
	String dilimeter;
	String variabless;
	String userRemote;
	String passRemote;
	String remotePath;
	String host;
	int port;
	Control c;

	public void getConfig(int id, String delimiter) throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM " + c.getDb_control() + "." + c.getTable_config() + " where config_id=?";
		pre = ConnectionDB.getConnection(c.getDb_control()).prepareStatement(sql);
		pre.setInt(1, id);
		ResultSet tmp = pre.executeQuery();
		tmp.next();
		config_name = tmp.getString("config_name");
		target_table = tmp.getString("target_table");
		file_type = tmp.getString("file_type");
		import_dir = tmp.getString("import_dir");
		success_fir = tmp.getString("success_dir");
		error_dir = tmp.getString("error_dir");
		column_list = tmp.getString("column_list");
		numOfcol = tmp.getInt("numofcol");
		dilimeter = tmp.getString("delimeter");
		variabless = tmp.getString("variabless");
		userRemote = tmp.getString("userRemote");
		passRemote = tmp.getString("passRemote");
		remotePath = tmp.getString("remotePath");
		host = tmp.getString("host");
		port = tmp.getInt("port");
		StringTokenizer tokens = new StringTokenizer(column_list, delimiter);
		while (tokens.hasMoreTokens()) {
			columnsList.add(tokens.nextToken());
		}
		System.out.println(remotePath);
		System.out.println("Get config: complete!!!!");

	}

	public void convertData() throws IOException, EncryptedDocumentException, InvalidFormatException, SQLException,
			ClassNotFoundException {

		File file = new File(import_dir);
//		URL url = new URL("http://drive.ecepvn.org:5000/index.cgi?launchApp=SYNO.SDS.App.FileStation3.Instance&launchParam=openfile%3D%252FECEP%252Fsong.nguyen%252FDW_2020%252F&fbclid=IwAR1uHCCSN5m35GwuGLdNM0z41d6kn8IeXXaUBsv006I6EwbyZ8diTKLablg");
		FileInputStream fis = new FileInputStream(file);
//		InputStreamReader fis = new InputStreamReader(url.openStream());
		// creating workbook instance that refers to .xls file
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		// creating a Sheet object to retrieve the object
		XSSFSheet sheet = wb.getSheetAt(0);
		// evaluating cell type
		FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
		boolean isFirstRow = true;

		for (Row row : sheet) // iteration over row using for each loop
		{

			ArrayList<Cell> ab = new ArrayList<Cell>();
			for (Cell cell : row) // iteration over cell using for each loop
			{
				ab.add(cell);

			}
			if (!isFirstRow) {
				System.out.println("f");
				insertData(ab);
			}
			isFirstRow = false;

		}
	}

// create table in destination database
	public String sqlCreatTable() {
		String preSql = "CREATE TABLE " + target_table + " (";
		preSql += columnsList.get(0) + " INT PRIMARY KEY NOT NULL,";
		for (int i = 1; i < numOfcol; i++) {
			preSql += columnsList.get(i) + " VARCHAR(100),";

		}
		preSql = preSql.substring(0, preSql.length() - 1) + ");";

		return preSql;

	}

	public void insertData(ArrayList<Cell> g) throws SQLException, ClassNotFoundException {

		String preSql = "INSERT INTO " + target_table + "(";
		for (int i = 0; i < numOfcol; i++) {
			preSql += columnsList.get(i) + ",";

		}
		preSql = preSql.substring(0, preSql.length() - 1) + ") VALUES ( ";
		PreparedStatement state;
		String a = preSql;
		a += g.get(0) + ",";
		for (int j = 1; j < g.size(); j++) {
			a += "\"" + g.get(j) + "\"" + ",";

		}
		a = a.substring(0, a.length() - 1) + ");";
		state = ConnectionDB.getConnection(c.getDb_staging()).prepareStatement(a);
		state.executeUpdate();

	}

	public void extractData() throws SQLException, EncryptedDocumentException, InvalidFormatException,
			ClassNotFoundException, IOException {
		String sqlCreateTb = sqlCreatTable();
		// create table
		System.out.println(sqlCreateTb);

		boolean tableStatus = false;
		try {
			System.out.println("Creating table " + target_table + ".......");
			PreparedStatement state = ConnectionDB.getConnection(c.getDb_staging()).prepareStatement(sqlCreateTb);
			state.execute();

			tableStatus = true;
			System.out.println("Create Table: Complete!!!");
			System.out.println("----------------------------------------------------------------");
		} catch (Exception e) {
			System.out.println("Can't create table " + target_table);
			System.out.println("----------------------------------------------------------------");
		}
		if (tableStatus) {

			System.out.println("Convert data!!!!!");
			convertData();
			System.out.println("Done!!!!!!");

		}

	}

	public void updateLog(String file_status, String file_name) throws EncryptedDocumentException,
			InvalidFormatException, ClassNotFoundException, SQLException, IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String file_timestamp = dtf.format(now);
		if (file_status == "ER") {
			extractData();
			String sql = "UPDATE  log SET file_status = ?, file_timestamp = ? WHERE file_name = ?";

			try {
				pre = (PreparedStatement) ConnectionDB.getConnection(c.getDb_control()).prepareStatement(sql);
				pre.setString(1, "TR");
				pre.setString(2, file_timestamp);
				pre.setString(3, file_name);
				pre.executeUpdate();

				System.out.println("Success update to log");
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	public int getNumOfdata() {
		return numOfdata;
	}

	public void setNumOfdata(int numOfdata) {
		this.numOfdata = numOfdata;
	}

	public int getNumOfcol() {
		return numOfcol;
	}

	public void setNumOfcol(int numOfcol) {
		this.numOfcol = numOfcol;
	}

	public String getListofcol() {
		return listofcol;
	}

	public void setListofcol(String listofcol) {
		this.listofcol = listofcol;
	}

	public ArrayList<String> getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList<String> columnsList) {
		this.columnsList = columnsList;
	}

	public String getConfig_name() {
		return config_name;
	}

	public void setConfig_name(String config_name) {
		this.config_name = config_name;
	}

	public String getTarget_table() {
		return target_table;
	}

	public void setTarget_table(String target_table) {
		this.target_table = target_table;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public String getImport_dir() {
		return import_dir;
	}

	public void setImport_dir(String import_dir) {
		this.import_dir = import_dir;
	}

	public String getSuccess_fir() {
		return success_fir;
	}

	public void setSuccess_fir(String success_fir) {
		this.success_fir = success_fir;
	}

	public String getError_dir() {
		return error_dir;
	}

	public void setError_dir(String error_dir) {
		this.error_dir = error_dir;
	}

	public String getColumn_list() {
		return column_list;
	}

	public void setColumn_list(String column_list) {
		this.column_list = column_list;
	}

	public String getDilimeter() {
		return dilimeter;
	}

	public void setDilimeter(String dilimeter) {
		this.dilimeter = dilimeter;
	}

	public String getVariabless() {
		return variabless;
	}

	public void setVariabless(String variabless) {
		this.variabless = variabless;
	}

	public String getUserRemote() {
		return userRemote;
	}

	public void setUserRemote(String userRemote) {
		this.userRemote = userRemote;
	}

	public String getPassRemote() {
		return passRemote;
	}

	public void setPassRemote(String passRemote) {
		this.passRemote = passRemote;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Control getC() {
		return c;
	}

	public void setC(Control c) {
		this.c = c;
	}

	public Config() {
		super();
	}

}
